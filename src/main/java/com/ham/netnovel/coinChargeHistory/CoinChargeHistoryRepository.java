package com.ham.netnovel.coinChargeHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoinChargeHistoryRepository extends JpaRepository<CoinChargeHistory,Long> {


    /**
     * 유저의 코인 사용 기록을 DB에서 찾아 반환하는 메서드
     * @param providerId 유저 정보
     * @return List CoinChargeHistory 엔티티 List로 반환
     */
    List<CoinChargeHistory> findByMemberProviderId(@Param("providerId") String providerId, Pageable pageable);

}
