package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NovelRankingServiceImplTest {

    private final NovelRankingService novelRankingService;

    @Autowired
    NovelRankingServiceImplTest(NovelRankingService novelRankingService) {
        this.novelRankingService = novelRankingService;
    }

    @Test
    void getNovelRankingEntity() {
        Long novelId = 1L;
        LocalDate todayDate = LocalDate.now();

        Optional<NovelRanking> novelRankingEntity = novelRankingService.getNovelRankingEntity(novelId, todayDate, RankingPeriod.DAILY);
        System.out.println(novelRankingEntity.get().getId());
    }

    //테스트 성공
    @Test
    void updateDailyRankings() {
        novelRankingService.updateDailyRankings();
    }
    //테스트 성공
    @Test
    void updateWeeklyRankings() {
        novelRankingService.updateWeeklyRankings();

    }

    @Test
    void updateMonthlyRankings() {
    }

    @Test
    void saveRankingToRedis(){
        RankingPeriod period = RankingPeriod.DAILY;
//        RankingPeriod period = RankingPeriod.WEEKLY;
        novelRankingService.saveRankingToRedis(period);

    }
    @Test
    void getRankingFromRedis(){

        String period = "daily";
        novelRankingService.getRankingFromRedis(period);

    }
}