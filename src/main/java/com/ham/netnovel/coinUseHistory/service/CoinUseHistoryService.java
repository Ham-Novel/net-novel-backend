package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.coinUseHistory.CoinUseHistory;
import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoinUseHistoryService {


   void saveCoinUseHistory(CoinUseCreateDto coinUseCreateDto);


   List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId, Pageable pageable);


   /**
    * 유저가 에피소드에 결제한 내역이 있는지 확인하는 메서드
    * @param providerId 유저 정보
    * @param episodeId 에피소드의 id
    * @return 결제 내역이 있을 경우 true, 결제 내역이 없을경우 false 반환
    */
   boolean hasMemberUsedCoinsForEpisode(String providerId, Long episodeId);




}
