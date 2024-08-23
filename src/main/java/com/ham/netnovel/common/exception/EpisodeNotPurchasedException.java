package com.ham.netnovel.common.exception;

import com.ham.netnovel.coinCostPolicy.dto.CostPolicyResponseDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;

public class EpisodeNotPurchasedException extends RuntimeException{//에피소드 결제 내역이 없을때 예외처리

    @Getter
    private final Integer coinCost; // 결제 실패 시, 응답할 가격 정책 정보


    public EpisodeNotPurchasedException(String message, Integer coinCost) {
        super(message);
        this.coinCost = coinCost;
    }

    public EpisodeNotPurchasedException(String message, Throwable cause, Integer coinCost) {
        super(message, cause);
        this.coinCost = coinCost;
    }
}
