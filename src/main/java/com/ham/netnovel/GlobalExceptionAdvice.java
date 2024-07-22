package com.ham.netnovel;

import com.ham.netnovel.common.exception.ServiceMethodException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * Opional 안에 Null일경우 예외처리, 클라이언트가 올바르지 않은 데이터 송부시 사용
     * @param ex
     * @return
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Optional 객체가 null입니다. NoSuchElementException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근입니다.");
    }


    /**
     * 유저가 넘겨준 파라미터 값이 유효하지 않을때 사용
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("파라미터가 유효하지 않습니다. IllegalArgumentException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
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


    /**
     * 데이터베이스 CRUD 작업 도중 발생하는 예외 처리
     * @param ex
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("데이터베이스 작업 에러, DataAccessException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 에러입니다 관리자에게 문의해주세요.");
    }


    /**
     * 서비스 계층 예외처리
     * @param serviceMethodException 서비스계층에서 사용하는 custom exception
     * @return
     */
    @ExceptionHandler(ServiceMethodException.class)
    public ResponseEntity<String> handleServiceMethodException(ServiceMethodException serviceMethodException) {
        log.error("서비스 계층 작업 에러, ServiceMethodException: {} ",serviceMethodException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 에러입니다 관리자에게 문의해주세요.");
    }





    //    RuntimeException 핸들링
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeExceptionException(Model model, RuntimeException ex) {
        log.error("errorMessage RuntimeException: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러입니다. 관리자에게 문의해주세요");
    }



    //등록되지 않은 예외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("errorMessage Exception: {} ",ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러입니다. 관리자에게 문의해주세요");
    }










}
