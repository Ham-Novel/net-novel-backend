package com.ham.netnovel.common.scheduler.config;

import com.ham.netnovel.common.scheduler.job.LatestDateBatchJob;
import com.ham.netnovel.common.scheduler.job.TotalFavoritesBatchJob;
import com.ham.netnovel.common.scheduler.job.TotalViewsBatchJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class NovelMetaDataQuartzConfig {

    @Bean
    public JobDetail totalViewUpdatejobDetail() {
        return JobBuilder.newJob(TotalViewsBatchJob.class)
                .withIdentity("totalViewUpdatejobDetail")
                .storeDurably()
                .build();
    }

    //소설 메타데이터의 총 조회수를 업데이트하는 트리거
    @Bean
    public Trigger totalViewUpdateTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(totalViewUpdatejobDetail())
                .withIdentity("novelTotalViewUpdateJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 30 * * * ?")) // 매일 10시, 15시, 20시에 실행
                .build();
    }

    @Bean
    public Trigger totalViewUpdateAtStartTrigger() {
        return createSingleRunTrigger(
                "totalViewUpdateAtStartTrigger"
                , totalViewUpdatejobDetail(),
                60);
    }

    @Bean
    public JobDetail totalFavoritesUpdateJobDetail() {
        return JobBuilder.newJob(TotalFavoritesBatchJob.class)
                .withIdentity("totalFavoritesUpdateJobDetail")
                .storeDurably()
                .build();
    }


    //소설메타데이터의 좋아요수를 업데이트하는 트리거, 매일 03시 10분, 14시 10분에 진행(무결성 훼손 방지)
    @Bean
    public Trigger totalFavoritesUpdateTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(totalFavoritesUpdateJobDetail())
                .withIdentity("totalFavoritesUpdateTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 10 3,14 * * ?")) // 매일 03시 10분과 14시 10분에 실행
                .build();
    }


    //소설메타데이터의 좋아요수를 업데이트하는 트리거, 애플리케이션 시작시 한번 실행

    @Bean
    public Trigger totalFavoritesUpdateTriggerAtStartTime() {
        return createSingleRunTrigger(
                "totalFavoritesUpdateTriggerAtStartTime",
                totalFavoritesUpdateJobDetail(),
                90);

    }

    @Bean
    public JobDetail latestDateUpdateJobDetail() {
        return JobBuilder.newJob(LatestDateBatchJob.class)
                .withIdentity("latestDateUpdateJobDetail")
                .storeDurably()
                .build();
    }

    //소설메타데이터의 최근 업데이트 시간을 변경하는 트리거, 매일 03시 20분에 진행(무결성 훼손 방지)
    @Bean
    public Trigger latestDateUpdateTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(totalFavoritesUpdateJobDetail())
                .withIdentity("latestDateUpdateTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 20 3 * * ?")) // 매일 새벽 03시 20분에 실행
                .build();
    }

    //소설메타데이터의 최근 업데이트 시간을 변경하는 트리거, 애플리케이션 시작시 한번만 실행
    @Bean
    public Trigger latestDateUpdateTriggerAtStartTime() {
        return createSingleRunTrigger(
                "latestDateUpdateTriggerAtStartTime"
                , latestDateUpdateJobDetail()
                , 120);

    }


    /**
     * 애플리케이션 시작시 한번만 실행되는 Trigger 정의 메서드
     *
     * @param triggerName    트리거 메서드 이름
     * @param jobDetail      실행시킬 jobDetail
     * @param delayInSeconds 애플리케이션 시작후 n초 뒤 실행
     * @return
     */
    private Trigger createSingleRunTrigger(String triggerName, JobDetail jobDetail, int delayInSeconds) {
        Date startTime = DateBuilder.futureDate(delayInSeconds, DateBuilder.IntervalUnit.SECOND);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)//실행시킬 JobDetail 메서드
                .withIdentity(triggerName)//식별자 이름
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(0)) // 단 한 번만 실행
                .startAt(startTime) // 지정된 시간에 실행 시작
                .build();
    }


}
