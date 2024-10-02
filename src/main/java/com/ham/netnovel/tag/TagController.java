package com.ham.netnovel.tag;

import com.ham.netnovel.tag.dto.TagDataDto;
import com.ham.netnovel.tag.dto.TagFindDto;
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
    public ResponseEntity<?> getTagNamesBySearchWord(@RequestParam(name = "searchWord") String searchWord) {

        //태그네임으로 검색
        List<String> tagNamesBySearchWord = tagService.getTagNamesBySearchWord(searchWord);

        //검색 결과 반환
        return ResponseEntity.ok(tagNamesBySearchWord);

    }


    /**
     * 태그 이름 또는 태그 ID를 기반으로 태그 정보를 검색하여 반환합니다.
     * <p>id 나 tagName 이 정확히 일치해야 합니다. 그렇지 않은경우 badRequest를 반환합니다.
     부분일치 검색은 <b>getTagNamesBySearchWord</b> API를 사용해주세요</p>
     *
     * <p>태그 이름이 기본값인 경우 태그 ID로 검색하고, 그렇지 않을 경우 태그 이름으로 검색합니다.
     * 태그 검색 조건이 비어 있을 경우 오류 응답을 반환합니다.</p>

     *
     * @param tagName 검색할 태그 이름 (기본값: "defaultName")
     * @param tagId   검색할 태그 ID (옵션)
     * @return 검색된 태그 데이터를 담은 ResponseEntity 객체, 검색 조건이 없을 경우 오류 응답 반환
     */

    @GetMapping("/tags/info")
    public ResponseEntity<?> getTagInfo(
            @RequestParam(name = "tagName", required = false, defaultValue = "defaultName") String tagName,
            @RequestParam(name = "tagId", required = false) Long tagId) {
        //태그 이름이 기본값, 태그id 가 null 이면 에러 메시지 전송
        if ("defaultName".equals(tagName) && tagId==null) {
            return ResponseEntity.badRequest().body("태그 검색 조건이 비어있습니다.");
        }

        //서비스 로직으로 데이터를 넘기기 위한 DTO 생성
        TagFindDto build = TagFindDto.builder()
                .tagName(tagName)
                .id(tagId)
                .build();
        //Tag 정보를 받아옴
        TagDataDto tagDto = tagService.getTagDto(build);

        //받아온 정보가 없을경우, badRequest 전송
        if (tagDto.getId() == null) {
            return ResponseEntity.badRequest().body("태그 정보가 없습니다. 잘못된 요청입니다.");
        }
        //정보가 있을경우 DTO 반환
        return ResponseEntity.ok(tagDto);
    }

}
