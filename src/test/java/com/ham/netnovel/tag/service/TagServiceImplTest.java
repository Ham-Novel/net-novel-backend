package com.ham.netnovel.tag.service;

import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.TagStatus;
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TagServiceImplTest {

    @Autowired
    TagService tagService;

    @Test
    void createTest() {
        //given
        TagCreateDto createDto = TagCreateDto.builder()
                .name("무협")
                .build();
        Long tagId = tagService.createTag(createDto);

        //when
        Tag tag = tagService.getTag(tagId).get();

        //then
        Assertions.assertThat(tag.getName()).isEqualTo(createDto.getName());
    }

    @Test
    void deleteTest() {
        //given
        Long id = 2L;
        TagDeleteDto deleteDto = TagDeleteDto.builder()
                .tagId(id)
                .build();
        tagService.deleteTag(deleteDto);

        //when
        Tag tag = tagService.getTag(2L).get();

        //then
        Assertions.assertThat(tag.getStatus()).isEqualTo(TagStatus.DELETED_BY_USER);

    }

}