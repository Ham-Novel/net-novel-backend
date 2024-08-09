package com.ham.netnovel.episodeViewCount;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.service.EpisodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
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
    void increaseViewCount() {
        Long episodeId = 20L;
        Optional<Episode> episode = episodeService.getEpisode(episodeId);
        episodeViewCountService.increaseViewCount(episode.get());
    }

    @Test
    void getTodayRanking(){
        LocalDate todayDate = LocalDate.now();

        episodeViewCountService.getDaliyRanking(todayDate);

    }

    @Test
    void getWeeklyRanking(){
        LocalDate todayDate = LocalDate.now();

        episodeViewCountService.getWeeklyRanking(todayDate);

    }
}