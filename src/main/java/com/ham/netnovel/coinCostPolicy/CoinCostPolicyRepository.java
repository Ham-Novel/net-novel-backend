package com.ham.netnovel.coinCostPolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoinCostPolicyRepository extends JpaRepository<CoinCostPolicy, Long> {

}
