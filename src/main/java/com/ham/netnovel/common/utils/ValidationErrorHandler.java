package com.ham.netnovel.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;


@Slf4j
public class ValidationErrorHandler {

    //static으로 인스턴스화 하지 않고 사용
    //@Valid 어노테이션 사용시 발생한 에러들을 list로 반환하는 메서드
    public static List<String> handleValidationErrors(BindingResult bindingResult) {
           return bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
    }


    public static List<String> handleValidationErrorMessages(BindingResult bindingResult,String methodName ) {

        //에러 메시지 List로 추출
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.error("{} }API 에러발생={}", methodName,errorMessages);
        //에러 메시지 반환
        return errorMessages;
    }
}
