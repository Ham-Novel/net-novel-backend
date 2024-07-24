package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.coinUseHistory.CoinUseHistory;
import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;

import java.util.List;

public interface CoinUseHistoryService {


   void saveCoinUseHistory(CoinUseCreateDto coinUseCreateDto);


   List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId);




}
