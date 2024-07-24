package com.ham.netnovel.common.exception;

public class NotEnoughCoinsException  extends RuntimeException{//코인 개수가 부족할때 사용하는 예외처리

    public NotEnoughCoinsException(String message) {
        super(message);
    }

    public NotEnoughCoinsException(String message, Throwable cause) {
        super(message, cause);
    }
}
