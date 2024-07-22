package com.ham.netnovel.common.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;


public class ValidationErrorHandler {

    //static으로 인스턴스화 하지 않고 사용
    //@Valid 어노테이션 사용시 발생한 에러들을 list로 반환하는 메서드
    public static List<String> handleValidationErrors(BindingResult bindingResult) {
           return bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
    }
}
