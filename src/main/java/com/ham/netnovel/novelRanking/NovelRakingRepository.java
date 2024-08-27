package com.ham.netnovel.novelRanking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NovelRakingRepository extends JpaRepository<NovelRanking, Long> {


    @Query("select nr from NovelRanking nr " +
            "where nr.novel.id = :novelId " +
            "and nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod =:rankingPeriod")
    Optional<NovelRanking> findNovelRankingsByDateAndPeriod(@Param("novelId") Long novelId,
                                                            @Param("rankingDate") LocalDate rankingDate,
                                                            @Param("rankingPeriod") RankingPeriod rankingPeriod);

    /**
     * 주어진 소설 ID 리스트, 랭킹 날짜, 및 랭킹 기간에 따라 소설 랭킹을 조회합니다.
     *
     * @param novelIds 랭킹을 조회할 소설들의 ID를 포함합니다.
     * @param rankingDate 이 날짜에 대한 랭킹 정보를 검색합니다.
     * @param rankingPeriod 랭킹이 속한 기간을 지정합니다.
     * @return 주어진 조건에 맞는 소설 랭킹 리스트를 반환합니다. 조건에 맞는 랭킹이 없으면 빈 리스트를 반환할 수 있습니다.
     */
    @Query("select nr " +
            "from NovelRanking nr " +
            "where nr.novel.id in :novelIds " +
            "and nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod = :rankingPeriod")
    List<NovelRanking> findNovelRankingsByDateAndPeriod(@Param("novelIds") List<Long> novelIds,
                                                        @Param("rankingDate") LocalDate rankingDate,
                                                        @Param("rankingPeriod") RankingPeriod rankingPeriod);


    /**
     * 랭킹 기록 날짜와, 랭킹 기간으로 엔티티 List를 찾는 메서드
     *
     * @param rankingDate   랭킹이 기록된 날짜
     * @param rankingPeriod 랭킹 기간(일간 주간 월간 전체)
     * @return List<NovelRanking>
     */
    @Query("select nr from NovelRanking nr " +
            "where nr.rankingDate = :rankingDate " +
            "and nr.rankingPeriod =:rankingPeriod")
    List<NovelRanking> findByRankingDateAndRankingPeriod(@Param("rankingDate") LocalDate rankingDate,
                                                         @Param("rankingPeriod") RankingPeriod rankingPeriod);


    @Query("select nr.novel, " +
            "sum(nr.score) as totalScore " +
            "from NovelRanking nr " +
            "where nr.rankingDate between :startDate and :endDate " +//날짜 범위 지정
            "and nr.rankingPeriod = :rankingPeriod " +
            "group by nr.novel " +
            "order by totalScore desc ")
//점수로 내림차순 정렬
    List<Object[]> findTotalScoreByDateAndRankingPeriod(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate,
                                                        @Param("rankingPeriod") RankingPeriod rankingPeriod);




    /**
     * 주어진 소설 ID 목록에 해당하는 소설들에 대해 특정 기간 동안 작성된 댓글 수를 조회하는 메서드입니다.
     *
     * 이 메서드는 소설과 에피소드, 그리고 댓글을 조인하여 특정 기간 내에 작성된 댓글 수를 소설별로 집계합니다.
     * 결과는 소설 엔티티와 해당 소설의 댓글 수를 포함하는 Object 배열 리스트로 반환됩니다.
     *
     * @param novelIds 댓글 수를 조회할 소설의 ID 목록입니다. 이 목록에 포함된 소설들에 대해서만 조회가 이루어집니다.
     * @param startDateTime 조회할 댓글의 시작 날짜 및 시간입니다. 이 날짜 및 시간을 포함한 이후의 댓글을 조회합니다.
     * @param endDateTime 조회할 댓글의 종료 날짜 및 시간입니다. 이 날짜 및 시간을 포함한 이전의 댓글을 조회합니다.
     * @return 주어진 소설 ID 목록에 대한 댓글 수를 포함하는 리스트를 반환합니다.
     *         반환되는 리스트의 각 요소는 소설과 댓글 수를 포함하는 Object 배열입니다.
     *         배열의 첫 번째 요소는 소설 엔티티 객체를, 두 번째 요소는 해당 소설의 총 댓글 수를 포함합니다.
     */
    @Query("select " +
            "n ," +//0번 인덱스는 노벨 엔티티
            "count(c.id) as totalComments " +//1번 인덱스는 댓글의 수
            "from Novel n " +
            "join Episode e on e.novel = n  " +//에피소드와 조인
            "join Comment c on c.episode = e " +//댓글과 조인
            "where n.id in :novelIds " +//파라미터로 받은 소설만 조회
            "and c.createdAt between :startDateTime and :endDateTime " +//댓글 생성 날짜 범위 제한
            "group by n ")//소설로 그룹화
    List<Object[]> findNovelAndCommentCount(@Param("novelIds") List<Long> novelIds,
                                            @Param("startDateTime") LocalDateTime startDateTime,
                                            @Param("endDateTime") LocalDateTime endDateTime);

}
