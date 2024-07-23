package com.ham.netnovel.novel;

import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelResponseDto;
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
    public ResponseEntity<NovelResponseDto> getNovel(@PathVariable("novelId") Long novelId) {
        NovelResponseDto responseData = parseResponseData(novelService.getNovel(novelId));
        log.debug("Get {}", responseData.toString());
        return ResponseEntity.ok(responseData);
    }

    @PostMapping
    public ResponseEntity<NovelResponseDto> createNovel(@RequestBody NovelCreateDto reqBody) {
        NovelResponseDto responseData = parseResponseData(novelService.createNovel(reqBody));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping
    public ResponseEntity<NovelResponseDto> createNovel(@RequestBody NovelUpdateDto reqBody) {
        NovelResponseDto responseData = parseResponseData(novelService.updateNovel(reqBody));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<NovelResponseDto> deleteNovel(
            @PathVariable("novelId") Long novelId,
            @RequestBody NovelDeleteDto reqBody) {
        NovelResponseDto responseData = parseResponseData(novelService.deleteNovel(reqBody));
        return ResponseEntity.ok(responseData);
    }

    public NovelResponseDto parseResponseData(Novel novel) {
        return NovelResponseDto.builder()
                .id(novel.getId())
                .title(novel.getTitle())
                .description(novel.getDescription())
                .authorName(novel.getAuthor().getNickName())
                .status(novel.getStatus())
                .build();
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
