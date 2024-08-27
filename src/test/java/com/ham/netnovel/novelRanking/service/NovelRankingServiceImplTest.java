package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
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


    @Test
    void getDailyRanking() {
        LocalDate todayDate = LocalDate.now();
        List<NovelRankingUpdateDto> dailyRanking = novelRankingService.calculateDailyRanking(todayDate);
        for (NovelRankingUpdateDto updateDto : dailyRanking) {
            System.out.println("novel id= "+ updateDto.getNovel().getId());
            System.out.println("상세정보= "+updateDto.toString());
            System.out.println("-----------------------------------------------------");
        }
    }

    @Test
    void getCommentCountByNovel(){
        List<Long> novelIds= new ArrayList<>();
        novelIds.add(1L);
        novelIds.add(2L);
        novelIds.add(12L);
        novelIds.add(10L);
        novelIds.add(15L);
        LocalDate todayDate = LocalDate.now();

        List<Object[]> commentCountByNovel = novelRankingService.getCommentCountByNovel(novelIds, todayDate.minusDays(1), todayDate);
        for (Object[] objects : commentCountByNovel) {
            System.out.println("Novel ID = "+((Novel)objects[0]).getId());
            System.out.println("Total Comment =" + (Long) objects[1]);
            System.out.println("----------------------------------------------");
        }

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
        novelRankingService.updateWeeklyNovelRankings();

    }

    @Test
    void updateMonthlyRankings() {
        novelRankingService.updateMonthlyNovelRankings();

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