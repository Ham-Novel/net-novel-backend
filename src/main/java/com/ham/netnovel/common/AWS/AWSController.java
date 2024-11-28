package com.ham.netnovel.common.AWS;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class AWSController {

    /**
     * AWS 인스턴스 Healh Check시 사용될 API입니다.
     * HTTP 200 응답하여 Health Check 상태를 정상으로 변경합니다.
     * @return ResponseEntity HTTP 200 응답을 담아 반환
     */


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        log.info("AWS Health Check Start");//health check 요청시 로그 생성
        return ResponseEntity.ok("Backend Connection = OK");//HTTP 200 응답 전송
    }

}
