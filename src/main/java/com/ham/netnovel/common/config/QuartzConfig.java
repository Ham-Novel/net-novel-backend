package com.ham.netnovel.common.config;


import com.ham.netnovel.episode.job.EpisodeViewCountUpdateJob;
import com.ham.netnovel.novelAverageRating.NovelAverageRatingJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {


    //소설의 평균 별점 갱신 JobDetail 설정
    @Bean
    public JobDetail novelAverageRatingJobDetail() {
        return JobBuilder.newJob(NovelAverageRatingJob.class)
                .withIdentity("novelAverageRatingJob")//식별자 설정
                .withDescription("Novel 평균 점수 업데이트, 별점이 있는 Novel 만 진행")//설명추가
                .storeDurably()
                .build();
    }

    //주간 Novel 조회수 랭킹 갱신 Trigger 설정, 1분마다 실행
    @Bean
    public Trigger novelAverageRatingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelAverageRatingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelAverageRatingTrigger")//트리거 식별자 설정
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()//단순 스케쥴 설정
                        .withIntervalInMinutes(10)//10분마다 갱신
                        .repeatForever())//무한반복설정
                .build();
    }


    //Redis에 저장된 에피소드 조회수 DB에 갱신
    @Bean
    public JobDetail episodeViewCountUpdateJobDetail() {
        return JobBuilder.newJob(EpisodeViewCountUpdateJob.class)
                .withIdentity("episodeViewCounUpdateJob")//식별자 설정
                .withDescription("Episode 조회수 업데이트,조회수가 있는 Episode 만 진행")//설명추가
                .storeDurably()
                .build();
    }

//    에피소드 조회수 Redis 에서 DB로 갱신 Trigger 설정, 10분마다 갱신
    @Bean
    public Trigger episodeViewCountUpdateTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(episodeViewCountUpdateJobDetail())//트리거와 novelAverageRatingJob 연결
                .withIdentity("episodeViewCountUpdateTrigger")//트리거 식별자 설정
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/10 * * * ?")) // 매시 0분, 10분, 20분, 30분, 40분, 50분에 실행
                .build();
    }


}
