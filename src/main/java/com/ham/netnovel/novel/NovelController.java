package com.ham.netnovel.novel;

import com.fasterxml.jackson.databind.JsonNode;
import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novel.service.NovelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/novel")
public class NovelController {
    private final NovelService novelService;

    public NovelController(NovelService novelService, EpisodeService episodeService) {
        this.novelService = novelService;
    }

    /**
     * 소설 상세 페이지 관련 정보 모두 요청
     * Novel: 엔티티 전체
     * Episode: 해당 Novel 총 화수, 총 조회수
     * EpisodeRating: 해당 Novel 별점
     * FavoriteNovel: 유저 관심 등록 수
     * @param novelId
     * @return
     */
    @GetMapping("/{novelId}")
    public ResponseEntity<Novel> getNovel(
            @PathVariable("novelId") Long novelId) {
        return ResponseEntity.ok(novelService.getNovel(novelId));
    }

    @PostMapping
    public ResponseEntity<Novel> createNovel(@RequestBody NovelCreateDto reqBody) {
        return ResponseEntity.ok(novelService.createNovel(reqBody));
    }

    @PutMapping
    public ResponseEntity<Novel> createNovel(@RequestBody NovelUpdateDto reqBody) {
        return ResponseEntity.ok(novelService.updateNovel(reqBody));
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<Novel> deleteNovel(
            @PathVariable("novelId") Long novelId,
            @RequestBody NovelDeleteDto reqBody) {
        ;
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
