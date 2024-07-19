package com.ham.netnovel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {


    //Optinal 벗겼을때 Null일경우 예외처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(Model model, NoSuchElementException ex) {
        log.error("errorMessage NoSuchElementException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러입니다. 관리자에게 문의해주세요");
    }


    /**
     * AuthenticationCredentialsNotFoundException 예외 핸들링
     * 유저 인증정보가 없을경우 던져지는 예외
     * @param ex 예외 객체
     * @return ResponseEntity bad request 전송
     */
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<String> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        log.error("errorMessage AuthenticationCredentialsNotFoundException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 정보가 없습니다.");
    }


//    RuntimeException 핸들링
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeExceptionException(Model model, RuntimeException ex) {
        log.error("errorMessage RuntimeException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러입니다. 관리자에게 문의해주세요");
    }







}
