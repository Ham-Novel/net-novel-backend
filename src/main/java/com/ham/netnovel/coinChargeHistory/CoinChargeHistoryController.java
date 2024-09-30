package com.ham.netnovel.coinChargeHistory;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;
import com.ham.netnovel.coinChargeHistory.service.CoinChargeHistoryService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api")
public class CoinChargeHistoryController {

    private final CoinChargeHistoryService coinChargeHistoryService;
    private final Authenticator authenticator;

    public CoinChargeHistoryController(CoinChargeHistoryService coinChargeHistoryService, Authenticator authenticator) {
        this.coinChargeHistoryService = coinChargeHistoryService;
        this.authenticator = authenticator;
    }

    @PostMapping("/coin-charge-history")
    public ResponseEntity<?> chargeMemberCoins(@Valid @RequestBody CoinChargeCreateDto coinChargeCreateDto,
                                               BindingResult bindingResult,
                                               Authentication authentication){
        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("chargeMemberCoins API 에러발생 ={}", bindingResult.getFieldError());
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보 저장
        coinChargeCreateDto.setProviderId(principal.getName());

        //DB에 기록 저장 후 유저가 소유한 코인 수 증가
        coinChargeHistoryService.saveCoinChargeHistory(coinChargeCreateDto);

        //응답
        return ResponseEntity.ok("충전완료");
    }






}
