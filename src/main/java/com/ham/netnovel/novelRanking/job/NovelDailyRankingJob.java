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
public class NovelDailyRankingJob implements Job {//일간 조회수 랭킹을 갱신하는 Job

    private final NovelRankingService novelRankingService;

    @Autowired
    public NovelDailyRankingJob(NovelRankingService novelRankingService) {
        this.novelRankingService = novelRankingService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("일간 조회수 랭킹 업데이트 시작");
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();
        // 1. 일간 랭킹 업데이트
        novelRankingService.updateDailyRankings(todayDate);

        // 2. Redis에 랭킹 저장
        novelRankingService.saveNovelRankingToRedis(RankingPeriod.DAILY);

        log.info("일간 조회수 랭킹 업데이트 완료 및 Redis 저장 완료");

    }
}
