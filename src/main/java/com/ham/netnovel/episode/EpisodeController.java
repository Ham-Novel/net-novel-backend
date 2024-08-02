package com.ham.netnovel.episode;

import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<EpisodeListItemDto>> getEpisodeListByNovel(@RequestParam("novelId") Long novelId) {
        List<EpisodeListItemDto> episodesByNovel = episodeService.getEpisodesByNovel(novelId);
        return ResponseEntity.ok(episodesByNovel);
    }

    @PostMapping
    public ResponseEntity<String> createEpisode(@Valid @RequestBody EpisodeCreateDto reqBody) {
        episodeService.createEpisode(reqBody);
        return ResponseEntity.ok("Episode Create Execution");
    }

}
