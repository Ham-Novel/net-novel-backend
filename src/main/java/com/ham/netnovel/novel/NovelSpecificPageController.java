package com.ham.netnovel.novel;

import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/novels")
public class NovelSpecificPageController {
    private final NovelService novelService;
    private final EpisodeService episodeService;

    public NovelSpecificPageController(NovelService novelService, EpisodeService episodeService) {
        this.novelService = novelService;
        this.episodeService = episodeService;
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
    @ResponseStatus(HttpStatus.OK)
    public Novel loadPageNovel(@PathVariable Long novelId) {
        Optional<Novel> novel = novelService.getNovel(novelId);
        return novel.orElse(null);
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
