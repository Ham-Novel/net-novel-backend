package com.ham.netnovel.common.exception;

import com.ham.netnovel.coinCostPolicy.dto.CostPolicyResponseDto;
import com.ham.netnovel.episode.dto.EpisodePaymentDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;

@Getter
public class EpisodeNotPurchasedException extends RuntimeException{//에피소드 결제 내역이 없을때 예외처리

    private final EpisodePaymentDto paymentInfo; // 결제 실패 시, 응답할 가격 정책 정보


    public EpisodeNotPurchasedException(String message, EpisodePaymentDto paymentInfo) {
        super(message);
        this.paymentInfo = paymentInfo;
    }

    public EpisodeNotPurchasedException(String message, Throwable cause, EpisodePaymentDto paymentInfo) {
        super(message, cause);
        this.paymentInfo = paymentInfo;
    }
}
