package com.ham.netnovel.novelTag;

import com.ham.netnovel.novelTag.dto.NovelTagListDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import com.ham.netnovel.tag.service.TagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "NovelTag", description = "소설 Tag와 관련된 작업")
public class NovelTagController {
    private final NovelTagService novelTagService;

    public NovelTagController(NovelTagService novelTagService) {
        this.novelTagService = novelTagService;
    }

    @GetMapping("/novels/{novelId}/tags")
    public ResponseEntity<?> readTagsByNovel(
            @PathVariable(name = "novelId") Long novelId
    ) {
        List<NovelTagListDto> tagsByNovel = novelTagService.getTagsByNovel(novelId);
        return ResponseEntity.ok(tagsByNovel);
    }
}
