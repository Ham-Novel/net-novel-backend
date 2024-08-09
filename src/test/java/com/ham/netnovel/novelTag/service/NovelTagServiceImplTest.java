package com.ham.netnovel.novelTag.service;

import com.ham.netnovel.novelTag.NovelTag;
import com.ham.netnovel.novelTag.NovelTagId;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagDeleteDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class NovelTagServiceImplTest {

    @Autowired
    NovelTagService novelTagService;

    @Test
    @Transactional
    void createNovelTag() {
        //given
        NovelTagCreateDto createDto = NovelTagCreateDto.builder()
                .novelId(30L)
                .tagName("무협")
                .build();

        //when
        NovelTagId id = novelTagService.createNovelTag(createDto);

        //then
        NovelTag novelTag = novelTagService.getNovelTag(id.getNovelId(), id.getTagId()).get();
        Assertions.assertThat(novelTag.getNovel().getId()).isEqualTo(createDto.getNovelId());
        Assertions.assertThat(novelTag.getTag().getName()).isEqualTo(createDto.getTagName());
    }

    @Test
    void deleteNovelTag() {
        //given
        NovelTagDeleteDto deleteDto = NovelTagDeleteDto.builder().novelId(1L).tagId(102L).build();

        //when
        novelTagService.deleteNovelTag(deleteDto);

        //then
    }

}