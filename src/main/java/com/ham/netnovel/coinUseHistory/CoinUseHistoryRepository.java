package com.ham.netnovel.coinUseHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoinUseHistoryRepository extends JpaRepository<CoinUseHistory,Long> {




    @Query("select c from CoinUseHistory c " +
            "where c.member.providerId = :providerId " +
            "order by c.createdAt desc ")
    List<CoinUseHistory> findByMemberProviderId(@Param("providerId")String providerId, Pageable pageable);


    @Query("select c from CoinUseHistory  c " +
            "where c.episode.id = :episodeId " +
            "and c.member.providerId = :providerId")
    Optional<CoinUseHistory> findByMemberAndEpisode(@Param("providerId") String providerId,
                                                    @Param("episodeId") Long episodeId);



}
