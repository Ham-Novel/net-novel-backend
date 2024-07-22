package com.ham.netnovel.common.exception;

public class ServiceMethodException extends RuntimeException{
    public ServiceMethodException(String message) {
        super(message);
    }

    public ServiceMethodException(String message, Throwable cause) {
        super(message, cause);
    }

}
