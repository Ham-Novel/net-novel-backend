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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/novel")
public class NovelController {
    private final NovelService novelService;

    public NovelController(NovelService novelService, EpisodeService episodeService) {
        this.novelService = novelService;
    }

    //ToDo @Valid 유저 인증 코드 추가
    @GetMapping("/{novelId}")
    public ResponseEntity<NovelDataDto> getNovel(@PathVariable("novelId") Long novelId) {
        return ResponseEntity.ok(novelService.getNovel(novelId));
    }

    @PostMapping
    public ResponseEntity<NovelDataDto> createNovel(@RequestBody NovelCreateDto reqBody) {
        return ResponseEntity.ok(novelService.createNovel(reqBody));
    }

    //ToDo updateNovel, deleteNovel MessageBody와 Url에 모두 novelId를 보내는 문제 해결해야 함
    @PutMapping("/{novelId}")
    public ResponseEntity<NovelDataDto> updateNovel(
            @PathVariable("novelId") Long novelId,
            @RequestBody NovelUpdateDto reqBody) {
        return ResponseEntity.ok(novelService.updateNovel(reqBody));
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<NovelDataDto> deleteNovel(
            @PathVariable("novelId") Long novelId,
            @RequestBody NovelDeleteDto reqBody) {
        return ResponseEntity.ok(novelService.deleteNovel(reqBody));
    }

    //ToDo List로 Novel 데이터들을 가져오는 getNovelList() 구현
    @GetMapping
    public ResponseEntity<List<NovelDataDto>> getNovelList () {
        return null;
    }

}
