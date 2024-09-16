package com.ham.netnovel.common.exception;

public class RepositoryMethodException  extends  RuntimeException{

    public RepositoryMethodException(String message) {
        super(message);
    }

    public RepositoryMethodException(String message, Throwable cause) {
        super(message, cause);
    }

}
