package com.ham.netnovel.novelRanking.job;

import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.service.NovelRankingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class NovelRankingPreviousDayJob implements Job {

    private final NovelRankingService novelRankingService;

    @Autowired
    public NovelRankingPreviousDayJob(NovelRankingService novelRankingService) {
        this.novelRankingService = novelRankingService;
    }

    //어제 일자 일간 조회수 랭킹 갱신
    //조회수는 23시59분까지 꾸준히 오르므로, 마지막 갱신은 다음날 진행
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("어제 일자 일간 조회수 랭킹 업데이트 시작");
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate previousDayDate = LocalDate.now().minusDays(1);
        // 1. 일간 랭킹 업데이트
        novelRankingService.updateDailyRankings(previousDayDate);
        //2. Redis에 랭킹 저장
        novelRankingService.saveRankingToRedis(RankingPeriod.DAILY);
        log.info("어제 일자 일간 조회수 랭킹 업데이트 완료");

    }
}
