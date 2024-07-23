package com.ham.netnovel.novel;

import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelDataDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/novel")
public class NovelController {
    private final NovelService novelService;

    public NovelController(NovelService novelService, EpisodeService episodeService) {
        this.novelService = novelService;
    }

    @GetMapping("/{novelId}")
    public ResponseEntity<NovelDataDto> getNovel(@PathVariable("novelId") Long novelId) {
        return ResponseEntity.ok(novelService.getNovel(novelId));
    }

    @PostMapping
    public ResponseEntity<NovelDataDto> createNovel(@RequestBody NovelCreateDto reqBody) {
        return ResponseEntity.ok(novelService.createNovel(reqBody));
    }

    @PutMapping
    public ResponseEntity<NovelDataDto> createNovel(@RequestBody NovelUpdateDto reqBody) {
        return ResponseEntity.ok(novelService.updateNovel(reqBody));
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<NovelDataDto> deleteNovel(
            @PathVariable("novelId") Long novelId,
            @RequestBody NovelDeleteDto reqBody) {
        return ResponseEntity.ok(novelService.deleteNovel(reqBody));
    }

/*
    // 등록된 모든 Novel List로 GET 요청
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Novel> getNovelList () {
        return novelService.getAllNovels();
    }
*/

}
