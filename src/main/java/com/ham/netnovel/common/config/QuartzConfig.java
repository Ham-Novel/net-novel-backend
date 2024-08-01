package com.ham.netnovel.common.config;


import com.ham.netnovel.novelAverageRating.NovelAverageRatingJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    // JobDetail을 정의하는 메서드
    @Bean
    public JobDetail novelAverageRatingJobDetail() {
        return JobBuilder.newJob(NovelAverageRatingJob.class)
                .withIdentity("novelAverageRatingJob")//식별자 설정
                .withDescription("Novel 평균 점수 업데이트, 별점이 있는 Novel 만 진행")//설명추가
                .storeDurably()
                .build();
    }

    // 트리거를 정의하는 메서드
    @Bean
    public Trigger novelAverageRatingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(novelAverageRatingJobDetail())//트리거와 novelAverageRatingJobDetail 연결
                .withIdentity("novelAverageRatingTrigger")//트리거 식별자 설정
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()//단순 스케쥴 설정
//                        .withIntervalInSeconds(5)//시간설정
                        .withIntervalInHours(12)
                        .repeatForever())//무한반복설정
                .build();
    }

}
