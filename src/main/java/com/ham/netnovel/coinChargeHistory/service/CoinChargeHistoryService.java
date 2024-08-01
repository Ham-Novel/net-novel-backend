package com.ham.netnovel.coinChargeHistory.service;

import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;
import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoinChargeHistoryService {


   /**
    * 유저의 코인 충전 기록을 저장하고, 유저의 코인 갯수를 늘리는 메서드
    * @param coinChargeCreateDto providerId(유저정보), amount(충전한 코인갯수), payment(충전금액) 멤버변수 가짐
    */
   void  saveCoinChargeHistory(CoinChargeCreateDto coinChargeCreateDto);


   /**
    * 유저 정보로 CoinChargeHistory 엔티티를 찾아 DTO로 반환하는 메서드
    * @param providerId 유저 정보
    * @return List MemberCoinChargeDto 로 변환해서 반환
    */
   List<MemberCoinChargeDto> getCoinChargeHistoryByMember(String providerId, Pageable pageable);


}
