package com.ham.netnovel.common.config;


import com.ham.netnovel.novelRanking.job.NovelRankingMonthlyJob;
import com.ham.netnovel.novelRanking.job.NovelRankingPreviousDayJob;
import com.ham.netnovel.novelRanking.job.NovelRankingWeeklyJob;
import com.ham.netnovel.novelRanking.job.NovelRankingDailyJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class NovelRankingQuartzConfig {


    //일간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelDailyRankingJobDetail() {
        return JobBuilder.newJob(NovelRankingDailyJob.class)
                .withIdentity("novelDailyRankingJob")//식별자 설정
                .withDescription("Update daily novel rankings.")//설명추가
                .storeDurably()
                .build();
    }


    //일간 Novel 조회수 랭킹 갱신 Trigger 설정, 10시 15시 20시에 갱신
    @Bean
    public Trigger novelDailyRatingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelDailyRankingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelDailyRatingTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10,15,20 * * ?")) // 매일 10시, 15시, 20시에 실행
                .build();
    }


    //어제 일자 일간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelRankingPreviousDayJobDetail() {
        return JobBuilder.newJob(NovelRankingPreviousDayJob.class)
                .withIdentity("novelRankingPreviousDayJob")//식별자 설정
                .withDescription("Update previous day daily novel rankings.")//설명추가
                .storeDurably()
                .build();

    }

    //어제 일자 일간 Novel 조회수 랭킹 갱신 Trigger 설정, 00시 1분에 실행
    @Bean
    public Trigger novelRankingPreviousDayTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelRankingPreviousDayJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelRankingPreviousDayTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("1 0 0 * * ?")) // 매일 00시 1분에 실행
                .build();
    }


    //주간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelRankingWeeklyJobDetail() {
        return JobBuilder.newJob(NovelRankingWeeklyJob.class)
                .withIdentity("novelRankingWeeklyJob")
                .withDescription("Update weekly novel rankings.")
                .storeDurably()
                .build();

    }

    //주간 Novel 조회수 랭킹 갱신 Trigger 설정, 매일 자정 00시 5분에 실행
    @Bean
    public Trigger novelRankingWeeklyTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelRankingWeeklyJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelRankingWeeklyTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("3 0 0 * * ?")) // 매일 00시 03분에 실행
                .startNow()
                .build();
    }



    //월간 Novel 조회수 랭킹 갱신 JobDetail 설정
    @Bean
    public JobDetail novelRankingMonthlyJobDetail() {
        return JobBuilder.newJob(NovelRankingMonthlyJob.class)
                .withIdentity("novelRankingMonthlyJob")
                .withDescription("Update monthly novel rankings.")
                .storeDurably()
                .build();
    }

    //주간 Novel 조회수 랭킹 갱신 Trigger 설정, 매일 자정 00시 5분에 실행
    @Bean
    public Trigger novelRankingMonthlyTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelRankingMonthlyJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelRankingMonthlyTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("5 0 0 * * ?")) // 매일 00시 05분에 실행
                .build();
    }




    //애플리케이션 시작시 어제 일자 일간 Novel 조회수 랭킹 갱신  Trigger 설정
    //시작 시점부터 3초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger novelRankingPreviousDayAtStartTime() {
        return createSingleRunTrigger("novelRankingPreviousDayAtStartTime",
                novelRankingPreviousDayJobDetail(),//실행시킬 JobDetail
                3);//딜레이 시간(초)
    }

    //애플리케이션 시작시 주간 Novel 조회수 랭킹 갱신 Trigger 설정
    //시작 시점부터 10초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger novelRankingWeeklyTriggerAtStartTime() {
        return createSingleRunTrigger("novelRankingWeeklyTriggerAtStartTime",
                novelRankingWeeklyJobDetail(),//실행시킬 JobDetail
                10);//딜레이 시간(초)
    }

    //애플리케이션 시작시 주간 Novel 조회수 랭킹 갱신 Trigger 설정
    //시작 시점부터 10초뒤 주간 Novel 랭킹 갱신
    @Bean
    public Trigger novelRankingMonthlyTriggerAtStartTime() {
        return createSingleRunTrigger("novelRankingMonthlyTriggerAtStartTime",
                novelRankingMonthlyJobDetail(),//실행시킬 JobDetail
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
