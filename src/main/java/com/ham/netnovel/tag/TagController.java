package com.ham.netnovel.tag;

import com.ham.netnovel.tag.dto.TagDataDto;
import com.ham.netnovel.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public ResponseEntity<TagDataDto> readTagsByNovel(
            @RequestParam() String tagName
    ) {
        return ResponseEntity.ok(tagService.readTagByName(tagName));
    }
}
