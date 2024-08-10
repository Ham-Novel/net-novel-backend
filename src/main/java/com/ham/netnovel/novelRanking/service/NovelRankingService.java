package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NovelRankingService {

    /**
     * NovelRaking 엔티티를 찾아 반환하는 메서드
     *
     * @param novelId       소설의 ID 값
     * @param rankingDate   랭킹이 기록된 날짜
     * @param rankingPeriod 일간,주간,월간,전체 기간중 해당되는 값
     * @return Optional NovelRanking
     */
    Optional<NovelRanking> getNovelRankingEntity(Long novelId, LocalDate rankingDate, RankingPeriod rankingPeriod);


    /**
     * 소설의 일일 랭킹을 업데이트하는 메서드
     * 해당 날짜의 조회수를 기반으로 새로운 랭킹 데이터를 생성하거나
     * 기존 랭킹 데이터를 업데이트
     */
    void updateDailyRankings();

    /**
     * 소설의 주간 랭킹을 업데이트하는 메서드
     * 이 메서드는 지정된 기간(어제 기준 7일 전까지)의 조회수를 기반으로
     * 주간 랭킹 데이터를 생성하거나 업데이트
     */
    void updateWeeklyRankings();

    void updateMonthlyRankings();


    void saveRankingToRedis(RankingPeriod rankingPeriod);

    List<Map<String, Object>> getRankingFromRedis(String period);


}
