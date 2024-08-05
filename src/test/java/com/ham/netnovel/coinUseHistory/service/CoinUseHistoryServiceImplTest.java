package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinUseHistoryServiceImplTest {
    private final CoinUseHistoryService coinUseHistoryService;

    @Autowired
    CoinUseHistoryServiceImplTest(CoinUseHistoryService coinUseHistoryService) {
        this.coinUseHistoryService = coinUseHistoryService;
    }

    @Test
    void saveCoinUseHistory() {

        String providerId = "";
        Long episodeId = 2306L;
        Integer amount = 2;

//        new
//        coinUseHistoryService.saveCoinUseHistory(providerId,episodeId,amount);


    }

    //테스트 성공
    @Test
    void getMemberCoinUseHistory() {
//        String providerId = "";
        String providerId = "33";//없는 유저, 빈리스트 반환됨

        Pageable pageable = PageRequest.of(0, 10);

        List<MemberCoinUseHistoryDto> memberCoinUseHistory = coinUseHistoryService.getMemberCoinUseHistory(providerId, pageable);

        if (memberCoinUseHistory.isEmpty()) {
            System.out.println("비었음");

        }

        for (MemberCoinUseHistoryDto memberCoinUseHistoryDto : memberCoinUseHistory) {
            System.out.println(memberCoinUseHistoryDto.toString());

        }


    }

    //테스트 완료
    @Test
    void hasMemberUsedCoinsForEpisode() {
        String providerId = "test100";
//      Long episodeId = 2305L;

        Long episodeId = null;


        //DB에서 레코드 조회
        boolean b = coinUseHistoryService.hasMemberUsedCoinsForEpisode(providerId, episodeId);

        //결과출력, 레코드가 있으면 true 없으면 false 반환
        System.out.println("결과= " + b);

    }
}