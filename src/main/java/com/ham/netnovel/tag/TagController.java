package com.ham.netnovel.tag;

import com.ham.netnovel.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api")

public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    /**
     * 주어진 검색어를 기반으로 태그 이름 목록을 반환하는 API입니다.
     * <p>
     * 검색어를 포함하는 태그의 이름을 List로 받아 전송합니다.
     * </p>
     * <p>
     * 만약 검색어 길이가 10자를 넘어가거나, 비어있으면 빈 리스트를 반환합니다.
     * </p>
     *
     * @param searchWord 검색할 태그의 일부 또는 전체 이름
     * @return 검색어와 일치하는 태그 이름 목록을 담은 ResponseEntity 객체
     */
    @GetMapping("/tags")
    public ResponseEntity<?> getTagNamesBySearchWord(@RequestParam(name = "searchWord")String searchWord){

        //태그네임으로 검색
        List<String> tagNamesBySearchWord = tagService.getTagNamesBySearchWord(searchWord);

        //검색 결과 반환
        return ResponseEntity.ok(tagNamesBySearchWord);

    }

}
