package com.ham.netnovel.common.scheduler.config;

import com.ham.netnovel.common.scheduler.job.LatestDateBatchJob;
import com.ham.netnovel.common.scheduler.job.TotalFavoritesBatchJob;
import com.ham.netnovel.common.scheduler.job.TotalViewsBatchJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public JobDetail totalFavoritesUpdateJobDetail(){
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
                .startNow() // 애플리케이션 시작 시 즉시 실행
                .build();
    }

    @Bean
    public JobDetail latestDateUpdateJobDetail(){
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
                .startNow() // 애플리케이션 시작 시 즉시 실행
                .build();
    }


}
