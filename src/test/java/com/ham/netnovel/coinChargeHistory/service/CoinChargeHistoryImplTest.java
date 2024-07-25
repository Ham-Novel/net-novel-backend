package com.ham.netnovel.coinChargeHistory.service;

import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinChargeHistoryImplTest {

    private final CoinChargeHistoryService coinChargeHistoryService;

    @Autowired
    CoinChargeHistoryImplTest(CoinChargeHistoryService coinChargeHistoryService) {
        this.coinChargeHistoryService = coinChargeHistoryService;
    }

    @Test
    void saveCoinChargeHistory() {

        CoinChargeCreateDto coinChargeCreateDto = new CoinChargeCreateDto();

        coinChargeCreateDto.setCoinAmount(5);

        //null 체크 테스트 IllegalArgumentException 던져짐
//        coinChargeCreateDto.setCoinAmount(null);

//        음수 테스트 IllegalArgumentException 던져짐
//        coinChargeCreateDto.setCoinAmount(-1);


      String payment = "5000.00";
        //올바르지 않은 자리수 테스트, IllegalArgumentException 던져짐
//        String payment =5000.000000000;


        coinChargeCreateDto.setPayment(payment);


        coinChargeCreateDto.setProviderId("test");

        //존재하지않은 유저 테스트 NoSuchElementException 던져짐
//        coinChargeCreateDto.setProviderId("error");

        coinChargeHistoryService.saveCoinChargeHistory(coinChargeCreateDto);

    }
}