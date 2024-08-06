package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EpisodeDetailServiceImplTest {

    private final EpisodeDetailService episodeDetailService;

    @Autowired
    EpisodeDetailServiceImplTest(EpisodeDetailService episodeDetailService) {
        this.episodeDetailService = episodeDetailService;
    }

    //테스트 성공
    @Test
    void getEpisodeDetail() {


        //결제 내역이 없으면 EpisodeNotPurchasedException로 던져짐
        String providerId="test";

        Long episodeId = 30001L;//존재하지 않는 Episode면 NoSuchElementException던져짐

        EpisodeDetailDto episodeDetail = episodeDetailService.getEpisodeDetail(providerId, episodeId);
        System.out.println(episodeDetail.toString());
    }
}