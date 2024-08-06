package com.ham.netnovel.episode.service;

import com.ham.netnovel.coinUseHistory.service.CoinUseHistoryService;
import com.ham.netnovel.common.exception.EpisodeNotPurchasedException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class EpisodeDetailServiceImpl implements EpisodeDetailService {

    private final EpisodeService episodeService;
    private final CoinUseHistoryService coinUseHistoryService;

    public EpisodeDetailServiceImpl(EpisodeService episodeService, CoinUseHistoryService coinUseHistoryService) {
        this.episodeService = episodeService;
        this.coinUseHistoryService = coinUseHistoryService;
    }


    @Override
    @Transactional
    public EpisodeDetailDto getEpisodeDetail(String providerId, Long episodeId) {


        //episode 엔티티 유무 확인, 없을경우 예외로 던짐
        Episode episode = episodeService.getEpisode(episodeId)
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

        //에피소드의 coinCost 객체에 저장
        Integer coinCost = episode.getCostPolicy().getCoinCost();
        //coinCost 검증, null 이거나 음수면 예외로 던짐
        TypeValidationUtil.validateCoinAmount(coinCost);

        //에피소드가 유료일경우 처리 로직
        if (coinCost > 0) {
            //유저의 에피소드 결제 내역을 확인, 있을경우 true 없을경우 false 반환
            boolean result = coinUseHistoryService.hasMemberUsedCoinsForEpisode(providerId, episodeId);
            //결제 내역이 없을경우 EpisodeNotPurchasedException 로 던짐
            if (!result) {
                throw new EpisodeNotPurchasedException("에피소드 결제 내역 없음, providerId = " + providerId + ", episodeId = " + episodeId);
            }
        }

        //에피소드 정보 DTO로 변환하여 반환
        return EpisodeDetailDto.builder()
                .episodeId(episodeId)
                .content(episode.getContent())
                .title(episode.getTitle())
                .build();


    }

}
