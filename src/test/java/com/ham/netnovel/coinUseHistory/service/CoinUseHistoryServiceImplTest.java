package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
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

        String providerId = "test";
//        String providerId = "teㄷㄷㄷㄷst";//존재하지 않은 유저 테스트, NoSuchElementException로 던져짐

//        Long episodeId = 3002L;
        Long episodeId = 3002L;//존재하지 않는 에피소드 테스트, NoSuchElementException로 던져짐


        Integer amount = 2;
//        Integer amount = 200000;        //유저 코인수가 사용 코인수보다 적은경우 테스트 , NotEnoughCoinsException로 던져짐

        CoinUseCreateDto dto = CoinUseCreateDto.builder()
                .usedCoins(amount)
                .episodeId(episodeId)
                .providerId(providerId)
                .build();

        coinUseHistoryService.saveCoinUseHistory(dto);


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