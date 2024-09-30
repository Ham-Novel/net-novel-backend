package com.ham.netnovel.coinUseHistory;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
import com.ham.netnovel.coinUseHistory.service.CoinUseHistoryService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api")
public class CoinUseHistoryController {

    private final CoinUseHistoryService coinUseHistoryService;

    private final Authenticator authenticator;


    public CoinUseHistoryController(CoinUseHistoryService coinUseHistoryService, Authenticator authenticator) {
        this.coinUseHistoryService = coinUseHistoryService;
        this.authenticator = authenticator;
    }

    /**
     * 유저의 코인 사용 기록을 저장하는 API(에피소드 열람시 코인 사용)
     * @param coinUseCreateDto 에피소드정보, 유저 정보를 담는 DTO
     * @param bindingResult DTO 검증 에러 객체
     * @param authentication 유저 인증 정보
     * @return
     */
    @PostMapping("/coin-use-histories")
    public ResponseEntity<String> createCoinUseHistory(@Valid @RequestBody CoinUseCreateDto coinUseCreateDto,
                                                  BindingResult bindingResult,
                                                  Authentication authentication){

        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            log.error("createCoinUseHistory API 에러발생={}",errorMessages);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보 저장
        coinUseCreateDto.setProviderId(principal.getName());

        coinUseHistoryService.saveCoinUseHistory(coinUseCreateDto);

        return ResponseEntity.ok("ok");

    }

    @GetMapping("/coin-use-histories/test")
    public String test1(){

        return "/coinUseHistory/test";

    }



}
