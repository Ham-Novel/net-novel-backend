package com.ham.netnovel.episode;

import com.ham.netnovel.episode.dto.EpisodeDataDto;
import com.ham.netnovel.episode.service.EpisodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/episode")
@Slf4j
public class EpisodeController {
    private final EpisodeService episodeService;

    @Autowired
    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping
    public ResponseEntity<List<EpisodeDataDto>> getEpisodeListByNovel(@RequestParam Long novelId) {
        List<EpisodeDataDto> episodesByNovel = episodeService.getEpisodesByNovel(novelId);
        return ResponseEntity.ok(episodesByNovel);
    }
}
