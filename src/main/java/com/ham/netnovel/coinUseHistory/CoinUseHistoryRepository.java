package com.ham.netnovel.coinUseHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoinUseHistoryRepository extends JpaRepository<CoinUseHistory,Long> {




    @Query("select c from CoinUseHistory c " +
            "where c.member.providerId = :providerId " +
            "order by c.createdAt desc ")
    List<CoinUseHistory> findByMemberProviderId(@Param("providerId")String providerId);

}
