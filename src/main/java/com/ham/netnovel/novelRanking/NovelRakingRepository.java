package com.ham.netnovel.novelRanking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface NovelRakingRepository extends JpaRepository<NovelRanking, Long> {


    @Query("select nr from NovelRanking nr " +
            "where nr.novel.id = :novelId " +
            "and nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod =:rankingPeriod")
   Optional<NovelRanking> findByNovelIdAndRankingAndRankingPeriod(@Param("novelId")Long novelId,
                                                                      @Param("rankingDate") LocalDate rankingDate,
                                                                      @Param("rankingPeriod")RankingPeriod rankingPeriod);

}
