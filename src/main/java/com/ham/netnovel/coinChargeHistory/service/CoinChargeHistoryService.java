package com.ham.netnovel.coinChargeHistory.service;

import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;

public interface CoinChargeHistoryService {


   /**
    * 유저의 코인 충전 기록을 저장하고, 유저의 코인 갯수를 늘리는 메서드
    * @param coinChargeCreateDto providerId(유저정보), amount(충전한 코인갯수), payment(충전금액) 멤버변수 가짐
    */
   void  saveCoinChargeHistory(CoinChargeCreateDto coinChargeCreateDto);


}
