package com.ham.netnovel.common.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TotalFavoritesBatchJob implements Job {


    private final JobLauncher jobLauncher;

    private final org.springframework.batch.core.Job novelTotalFavoritesUpdateJob;

    @Autowired
    public TotalFavoritesBatchJob(JobLauncher jobLauncher, org.springframework.batch.core.Job novelTotalFavoritesUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.novelTotalFavoritesUpdateJob = novelTotalFavoritesUpdateJob;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("소설 좋아요 메타 데이터 업데이트 배치 작업 시작");
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 매번 새로운 파라미터 필요
                    .toJobParameters();
            // Spring Batch Job 실행
            jobLauncher.run(novelTotalFavoritesUpdateJob, params);
            log.info("소설 좋아요 메타 데이터 업데이트 배치 작업 완료");
        } catch (Exception e) {
            log.error("TotalFavoritesBatchJob 실행 에러");
            throw new JobExecutionException(e);
        }
    }
}

