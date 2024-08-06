package com.ham.netnovel.common.exception;

public class EpisodeNotPurchasedException extends RuntimeException{//에피소드 결제 내역이 없을때 예외처리

    public EpisodeNotPurchasedException(String message) {
        super(message);
    }

    public EpisodeNotPurchasedException(String message, Throwable cause) {
        super(message, cause);
    }
}
