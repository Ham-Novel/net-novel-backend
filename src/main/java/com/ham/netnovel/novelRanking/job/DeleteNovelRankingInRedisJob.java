package com.ham.netnovel.novelRanking.job;

import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.service.NovelRankingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteNovelRankingInRedisJob implements Job {

    private final NovelRankingService novelRankingService;

    public DeleteNovelRankingInRedisJob(NovelRankingService novelRankingService) {
        this.novelRankingService = novelRankingService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Redis 랭킹 정보 삭제 시작");
        novelRankingService.deleteNovelRankingInRedis(RankingPeriod.DAILY);
        novelRankingService.deleteNovelRankingInRedis(RankingPeriod.WEEKLY);
        novelRankingService.deleteNovelRankingInRedis(RankingPeriod.MONTHLY);
        log.info("Redis 랭킹 정보 삭제 완료");


    }
}
