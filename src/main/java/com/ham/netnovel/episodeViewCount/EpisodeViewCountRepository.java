package com.ham.netnovel.episodeViewCount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EpisodeViewCountRepository extends JpaRepository<EpisodeViewCount, Long> {


    /**
     * 에피소드의 ID와 날짜로 엔티티를 찾는 메서드
     *
     * @param episodeId 에피소드 ID
     * @param viewDate  날짜(연 월 일)
     * @return Optional EpisodeViewCount
     */
    @Query("select ev from EpisodeViewCount ev " +
            "where ev.episode.id = :episodeId " +
            "and ev.viewDate = :viewDate")
    Optional<EpisodeViewCount> findByEpisodeIdAndViewDate(@Param("episodeId") Long episodeId,
                                                          @Param("viewDate") LocalDate viewDate);



    @Query("select n AS novel, " +
            "sum(ev.viewCount)  As totalViews, " +
            "ev.viewDate as viewDate " +
            "from Novel n " +
            "join n.episodes e " +
            "join EpisodeViewCount ev ON ev.episode = e " +
            "where ev.viewDate between :startDate and :endDate " +//날짜 범위 설정
            "group by n, ev.viewDate")
    List<Object[]> findNovelTotalViews(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);






}
