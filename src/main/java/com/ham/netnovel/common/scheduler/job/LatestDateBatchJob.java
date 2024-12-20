package com.ham.netnovel.common.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LatestDateBatchJob implements Job {

    private final JobLauncher jobLauncher;

    private final org.springframework.batch.core.Job novelLatestEpisodeAtUpdateJob;

    public LatestDateBatchJob(JobLauncher jobLauncher, org.springframework.batch.core.Job novelLatestEpisodeAtUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.novelLatestEpisodeAtUpdateJob = novelLatestEpisodeAtUpdateJob;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        log.info("Novel Meta Data: 최근 업로드 날짜 배치 작업 시작");
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 매번 새로운 파라미터 필요
                    .toJobParameters();
            // Spring Batch Job 실행
            jobLauncher.run(novelLatestEpisodeAtUpdateJob, params);

            log.info("Novel Meta Data: 최근 업로드 날짜 배치 작업 완료");
        } catch (Exception e) {
            log.error("LatestDateBatchJob 실행 에러");
            throw new JobExecutionException(e);
        }
    }
}

