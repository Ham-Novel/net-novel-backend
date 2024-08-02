package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyDeleteDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyUpdateDto;

import java.util.Optional;

public interface CoinCostPolicyService {


    /**
     * policyId 값으로 DB에서 Entity 가져오는 메서드. 사용 시 Null 체크 필수.
     * @return Optional<CoinCostPolicy>
     */
    Optional<CoinCostPolicy> getPolicyEntity(Long policyId);

    /**
     * 가격 정책을 DB에 새로 생성하는 메서드
     */
    void createPolicy(CostPolicyCreateDto costPolicyCreateDto);


    /**
     * DB 내 가격 정책을 수정하는 메서드
     */
    void updatePolicy(CostPolicyUpdateDto costPolicyUpdateDto);


    /**
     * DB 내 가격 정책을 삭제 상태로 바꾸는 메서드
     */
    void deletePolicy(CostPolicyDeleteDto costPolicyDeleteDto);

    /**
     * CoinCostPolicy의 제약 조건 Validate
     */
    void validatePolicy(Long costPolicyId, Integer episodeNumber);

}
