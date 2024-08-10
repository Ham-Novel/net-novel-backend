package com.ham.netnovel.novelRanking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NovelRakingRepository extends JpaRepository<NovelRanking, Long> {


    @Query("select nr from NovelRanking nr " +
            "where nr.novel.id = :novelId " +
            "and nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod =:rankingPeriod")
   Optional<NovelRanking> findByNovelIdAndRankingAndRankingPeriod(@Param("novelId")Long novelId,
                                                                      @Param("rankingDate") LocalDate rankingDate,
                                                                      @Param("rankingPeriod")RankingPeriod rankingPeriod);


    /**
     * 랭킹 기록 날짜와, 랭킹 기간으로 엔티티 List를 찾는 메서드
     * @param rankingDate 랭킹이 기록된 날짜
     * @param rankingPeriod 랭킹 기간(일간 주간 월간 전체)
     * @return      List<NovelRanking>
     */
    @Query("select nr from NovelRanking nr " +
            "where nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod =:rankingPeriod")
    List<NovelRanking> findByRankingDateAndRankingPeriod(@Param("rankingDate") LocalDate rankingDate,
                                                    @Param("rankingPeriod")RankingPeriod rankingPeriod);

}
