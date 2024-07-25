package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinUseHistoryServiceImplTest {
  private final   CoinUseHistoryService coinUseHistoryService;

  @Autowired
    CoinUseHistoryServiceImplTest(CoinUseHistoryService coinUseHistoryService) {
        this.coinUseHistoryService = coinUseHistoryService;
    }

    @Test
    void saveCoinUseHistory() {

        String providerId = "";
        Long episodeId = 2306L;
        Integer amount =2;

//        new
//        coinUseHistoryService.saveCoinUseHistory(providerId,episodeId,amount);


    }

    //테스트 성공
    @Test
    void getMemberCoinUseHistory(){
//        String providerId = "";
        String providerId = "33";//없는 유저, 빈리스트 반환됨


        List<MemberCoinUseHistoryDto> memberCoinUseHistory = coinUseHistoryService.getMemberCoinUseHistory(providerId);

        if (memberCoinUseHistory.isEmpty()){
            System.out.println("비었음");

        }

        for (MemberCoinUseHistoryDto memberCoinUseHistoryDto : memberCoinUseHistory) {
            System.out.println(memberCoinUseHistoryDto.toString());

        }


    }
}