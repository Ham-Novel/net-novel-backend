package com.ham.netnovel.novelTag.service;

import com.ham.netnovel.novelTag.NovelTag;
import com.ham.netnovel.novelTag.NovelTagId;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagDeleteDto;
import com.ham.netnovel.novelTag.dto.NovelTagListDto;

import java.util.List;
import java.util.Optional;

public interface NovelTagService {

    /**
     * novel id와 tag id로 NovelTag 엔티티 가져오는 메서드
     * @param tagId Tag PK 값
     * @param novelId Novel PK 값
     * @return Optional
     */
    Optional<NovelTag> getNovelTag(Long novelId, Long tagId);


    /**
     * 특정 작품의 모든 태그를 반환
     * @param novelId
     * @return List
     */
    List<NovelTagListDto> getTagsByNovel(Long novelId);

    /**
     * Novel에 Tag를 할당을 배열로 하는 메서드
     * @param createDtoList novel id, tag 이름으로 구성된 dto의 list
     * @return NovelTagId
     */
    void createNovelTags(List<NovelTagCreateDto> createDtoList);

    /**
     * Novel에 Tag를 핟당하는 메서드
     * @param createDto novel id, tag 이름
     * @return NovelTagId
     */
    NovelTagId createNovelTag(NovelTagCreateDto createDto);

//    NovelTagId createNovelTag(Long novelId, Long tagId);

    /**
     * Novel에서 Tag를 제거하는 메서드
     * @param deleteDto novel id, tag id
     */
    void deleteNovelTag(NovelTagDeleteDto deleteDto);
}
