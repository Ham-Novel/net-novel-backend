package com.ham.netnovel.episodeViewCount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EpisodeViewCountRepository extends JpaRepository<EpisodeViewCount,Long> {


    /**
     * 에피소드의 ID와 날짜로 엔티티를 찾는 메서드
     * @param episodeId 에피소드 ID
     * @param viewDate 날짜(연 월 일)
     * @return Optional EpisodeViewCount
     */
    @Query("select ev from EpisodeViewCount ev " +
            "where ev.episode.id = :episodeId " +
            "and ev.viewDate = :viewDate")
    Optional<EpisodeViewCount> findByEpisodeIdAndViewDate(@Param("episodeId") Long episodeId,
                                                          @Param("viewDate")LocalDate viewDate);


    /**
     * 특정 날짜에 대한 조회수를 소설별로 그룹화하여 반환하는 메서드
     * @param viewDate 메서드가 실행될 날짜
     * @return List Novel 엔티티와 tatalView(소설 총 조회수) 를 Object[]에 담아 반환
     *         각 배열의 첫 번째 요소는 소설 엔티티(Novel)이고, 두 번째 요소는 총 조회수
     */
    @Query("select n AS novel, " +
            "sum(ev.viewCount)  As totalViews " +
            "from Novel  n " +
            "join n.episodes e " +
            "join EpisodeViewCount ev On ev.episode = e " +
            "where ev.viewDate =:viewDate " +
            "group by n.id ")
    List<Object[]> findTodayNovelTotalViews(@Param("viewDate") LocalDate viewDate);


    /**
     * 한주간 소설의 총 조회수를 조회하여 소설별로 그룹화하여 반환하는 메서드
     * @param yesterday 메서드 실행 날짜로부터 하루전 연월일 정보
     * @param startDate 메서드 실행 날짜로부터 7일전 연월일 정보
     * @return
     */
    @Query("select n AS novel, " +
            "sum(ev.viewCount)  As totalViews " +
            "from Novel  n " +
            "join n.episodes e " +
            "join EpisodeViewCount ev On ev.episode = e " +
            "where ev.viewDate between :startDate and :yesterday " +
            "group by n.id ")
    List<Object[]> findWeeklyNovelTotalViews(@Param("yesterday") LocalDate yesterday,
                                             @Param("startDate") LocalDate startDate);







}
