package com.ham.netnovel.episodeViewCount;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.episodeViewCount.service.EpisodeViewCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class EpisodeEpisodeViewCountServiceImplTest {
    private final EpisodeViewCountService episodeViewCountService;

    private final EpisodeService episodeService;
    @Autowired
    EpisodeEpisodeViewCountServiceImplTest(EpisodeViewCountService episodeViewCountService, EpisodeService episodeService) {
        this.episodeViewCountService = episodeViewCountService;
        this.episodeService = episodeService;
    }


    @Test
    void getTodayRanking(){
        LocalDate todayDate = LocalDate.now();

        episodeViewCountService.getDailyRanking(todayDate);

    }

    @Test
    void getWeeklyRanking(){
        LocalDate todayDate = LocalDate.now();

        episodeViewCountService.getWeeklyRanking(todayDate);

    }

    @Test
    void increaseViewCountRedis(){
        Long episodeId = 3L;
        int max = 20;//조회수를 몇번 증가시킬건지
        for (int i = 0; i <max ; i++) {
            episodeViewCountService.incrementEpisodeViewCountInRedis(episodeId);

        }
    }

    @Test
    void getEpisodeViewCountFromRedis(){
        List<ViewCountIncreaseDto> episodeViewCountFromRedis = episodeViewCountService.getEpisodeViewCountFromRedis();
        for (ViewCountIncreaseDto result : episodeViewCountFromRedis) {
            System.out.println("결과");
            System.out.println(result.toString());

        }

    }
}