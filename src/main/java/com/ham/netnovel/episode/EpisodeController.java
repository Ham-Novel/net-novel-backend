package com.ham.netnovel.episode;

import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.service.EpisodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<List<EpisodeListItemDto>> getEpisodesByNovel(
            @RequestParam(name = "novelId", required = true) Long novelId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {

        //Pageable 객체 생성. null or 음수이면 예외 발생
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        if (sortBy.equals("recent")) {
            return ResponseEntity.ok(episodeService.getEpisodesByNovelSortByRecent(novelId, pageable));
        } else if (sortBy.equals("initial")) {
            return ResponseEntity.ok(episodeService.getEpisodesByNovelSortByInitial(novelId, pageable));
        } else {
            //정렬 값이 없으면 예외 발생
            throw new IllegalArgumentException("getEpisodesByNovel: invalid sortBy option");

        }
    }
}
