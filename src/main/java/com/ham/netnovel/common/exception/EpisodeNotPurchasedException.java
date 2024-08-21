package com.ham.netnovel.common.exception;

import com.ham.netnovel.coinCostPolicy.dto.CostPolicyResponseDto;
import lombok.Getter;

public class EpisodeNotPurchasedException extends RuntimeException{//에피소드 결제 내역이 없을때 예외처리

    @Getter
    private final CostPolicyResponseDto coinCostPolicy;


    public EpisodeNotPurchasedException(String message, CostPolicyResponseDto coinCostPolicy) {
        super(message);
        this.coinCostPolicy = coinCostPolicy;
    }

    public EpisodeNotPurchasedException(String message, Throwable cause, CostPolicyResponseDto coinCostPolicy) {
        super(message, cause);
        this.coinCostPolicy = coinCostPolicy;
    }
}
