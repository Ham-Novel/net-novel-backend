package com.ham.netnovel.coinUseHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CoinUseHistoryRepository extends JpaRepository<CoinUseHistory, Long> {


    @Query("select c from CoinUseHistory c " +
            "where c.member.providerId = :providerId " +
            "order by c.createdAt desc ")
    List<CoinUseHistory> findByMemberProviderId(@Param("providerId") String providerId, Pageable pageable);


    @Query("select c from CoinUseHistory  c " +
            "where c.episode.id = :episodeId " +
            "and c.member.providerId = :providerId " +
            "order by c.updatedAt")
    Optional<CoinUseHistory> findByMemberAndEpisode(@Param("providerId") String providerId,
                                                    @Param("episodeId") Long episodeId);

    /**
     * 주어진 소설 ID 목록에 대해 특정 기간 동안 사용된 코인의 총합을 조회합니다.
     *
     * <p>이 메서드는 주어진 소설 ID 목록과 시작일, 종료일을 기준으로 {@link CoinUseHistory} 엔티티에서
     * 각 소설에 대해 사용된 코인의 총 수를 계산합니다. 결과는 소설 ID, 소설 제목, 총 사용된 코인 수,
     * 작가의 providerId를 포함하는 배열의 리스트로 반환됩니다.</p>
     *
     * @param novelIds 소설 ID 목록
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 주어진 기간 동안 각 소설의 총 사용된 코인 수를 포함하는  {@link Object} 타입 {@link List} 객체
     * 배열, 각 배열은 [소설 ID, 소설 제목, 총 사용된 코인 수, 작가의 providerId]를 포함합니다.
     */
    @Query("select " +
            "e.novel.id, " +//소설 id
            "e.novel.title, " +//소설 제목
            "sum(c.amount), " +//소설에 사용된 코인의 총 수
            "e.novel.author.providerId " +//작가의 providerId
            "from CoinUseHistory c " +
            "join c.episode e " +
            "where e.novel.id in :novelIds " +
            "and c.createdAt between :startDate and :endDate " +
            "group by e.novel.id")
    List<Object[]> findByNovelAndDateTime(
            @Param("novelIds") List<Long> novelIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
