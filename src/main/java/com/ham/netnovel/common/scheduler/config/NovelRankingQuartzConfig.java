package com.ham.netnovel.common.scheduler.config;


import com.ham.netnovel.novelRanking.job.*;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class NovelRankingQuartzConfig {


    //일간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelDailyRankingJobDetail() {
        return JobBuilder.newJob(NovelDailyRankingJob.class)
                .withIdentity("novelDailyRankingJobDetail")//식별자 설정
                .withDescription("Update daily novel rankings.")//설명추가
                .storeDurably()
                .build();
    }


    //일간 Novel 조회수 랭킹 갱신 Trigger 설정, 03시 10시 14시 20시에 갱신
    @Bean
    public Trigger novelDailyRankingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelDailyRankingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelDailyRatingTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 3,10,14,20 * * ?")) // 매일 3시, 10시, 14시, 20시에 실행
                .build();
    }


    //어제 일자 일간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelPreviousDayRankingJobDetail() {
        return JobBuilder.newJob(NovelPreviousDayRankingJob.class)
                .withIdentity("novelPreviousDayRankingJobDetail")//식별자 설정
                .withDescription("Update previous day daily novel rankings.")//설명추가
                .storeDurably()
                .build();

    }

    //어제 일자 일간 Novel 조회수 랭킹 갱신 Trigger 설정, 03시 05분에 실행
    @Bean
    public Trigger novelPreviousDayRankingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelPreviousDayRankingJobDetail())//트리거와 JobDetail 연결
                .withIdentity("novelPreviousDayRankingTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 5 3 * * ?")) // 매일 03시 5분에 실행
                .build();
    }


    //주간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelWeeklyRankingJobDetail() {
        return JobBuilder.newJob(NovelWeeklyRankingJob.class)
                .withIdentity("novelWeeklyRankingJobDetail")
                .withDescription("Update weekly novel rankings.")
                .storeDurably()
                .build();

    }

    //주간 Novel 조회수 랭킹 갱신 Trigger 설정, 매일 03시 10분에 실행
    @Bean
    public Trigger novelWeeklyRankingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelWeeklyRankingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelWeeklyRankingTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 10 3 * * ?")) // 매일 03시 05분에 실행
                .startNow()
                .build();
    }


    //월간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelMonthlyRankingJobDetail() {
        return JobBuilder.newJob(NovelMonthlyRanking.class)
                .withIdentity("novelMonthlyRankingJobDetail")
                .withDescription("Update monthly novel rankings.")
                .storeDurably()
                .build();
    }

    //주간 Novel 조회수 랭킹 갱신 Trigger 설정, 매일 자정 03시 15분에 실행
    @Bean
    public Trigger novelMonthlyRankingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelMonthlyRankingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelMonthlyRankingTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 15 3 * * ?")) // 매일 03시 15분에 실행
                .build();
    }

    //Redis에 모든 랭킹 정보를 삭제하는 JobDetail 설정
    @Bean
    public JobDetail deleteNovelRankingInRedisJobDetail() {
        return JobBuilder.newJob(DeleteNovelRankingInRedisJob.class)
                .withIdentity("deleteNovelRankingInRedisJobDetail")
                .withDescription("Delete novel rankings in Redis.")
                .storeDurably()
                .build();
    }

    //Redis에 모든 랭킹 정보를 삭제하는 Trigger 설정
    //애플리케이션 시작 시 Redis의 모든 랭킹 데이터 삭제

    @Bean
    public Trigger deleteNovelRankingInRedisTrigger() {
       return createSingleRunTrigger("deleteNovelRankingInRedisTrigger",
                deleteNovelRankingInRedisJobDetail(),
                0);//애플리케이션 실행 즉시, Redis 랭킹 정보 초기화

    }


    //애플리케이션 시작시 어제 일자 일간 Novel 조회수 랭킹 갱신  Trigger 설정
    //시작 시점부터 3초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger novelPreviousDayRankingAtStartTime() {
        return createSingleRunTrigger("novelPreviousDayRankingAtStartTime",
                novelPreviousDayRankingJobDetail(),//실행시킬 JobDetail
                3);//딜레이 시간(초)
    }


    @Bean
    public Trigger novelDailyRankingTriggerAtStartTime() {
        return createSingleRunTrigger("novelDailyRankingTriggerAtStartTime",
                novelDailyRankingJobDetail(),//실행시킬 JobDetail
                5);//딜레이 시간(초)


    }

    //애플리케이션 시작시 주간 Novel 조회수 랭킹 갱신 Trigger 설정
    //시작 시점부터 10초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger novelWeeklyRankingTriggerAtStartTime() {
        return createSingleRunTrigger("novelWeeklyRankingTriggerAtStartTime",
                novelWeeklyRankingJobDetail(),//실행시킬 JobDetail
                10);//딜레이 시간(초)
    }

    //애플리케이션 시작시 주간 Novel 조회수 랭킹 갱신 Trigger 설정
    //시작 시점부터 10초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger monthlyRankingTriggerAtStartTime() {
        return createSingleRunTrigger("monthlyRankingTriggerAtStartTime",
                novelMonthlyRankingJobDetail(),//실행시킬 JobDetail
                15);//딜레이 시간(초)
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
