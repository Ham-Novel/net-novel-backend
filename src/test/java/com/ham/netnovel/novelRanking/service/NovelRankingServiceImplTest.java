package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        LocalDate todayDate = LocalDate.now();

        novelRankingService.updateDailyRankings(todayDate);
    }
    //테스트 성공
    @Test
    void updateWeeklyRankings() {
        novelRankingService.updateWeeklyRankings();

    }

    @Test
    void updateMonthlyRankings() {
        novelRankingService.updateMonthlyRankings();

    }

    @Test
    void saveRankingToRedis(){
//        RankingPeriod period = RankingPeriod.DAILY;
//        RankingPeriod period = RankingPeriod.WEEKLY;
        RankingPeriod period = RankingPeriod.MONTHLY;
        novelRankingService.saveNovelRankingToRedis(period);

    }
    @Test
    void getRankingFromRedis(){

//        String period = "daily";
        String period = "weekly";
//        String period = "monthly";
        Integer startIndex = 0;
        Integer endIndex = 3;

        List<Map<String, Object>> rankingFromRedis = novelRankingService.getNovelRankingFromRedis(period,startIndex,endIndex);
        for (Map<String, Object> result : rankingFromRedis) {
            System.out.println("noelid= "+result.get("novelId"));
            System.out.println("랭킹= "+result.get("ranking"));


        }

    }
}