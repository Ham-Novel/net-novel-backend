package com.ham.netnovel.coinChargeHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoinChargeHistoryRepository extends JpaRepository<CoinChargeHistory,Long> {


    /**
     *
     * @param providerId
     * @return
     */
    List<CoinChargeHistory> findByMemberProviderId(@Param("providerId") String providerId);

}
