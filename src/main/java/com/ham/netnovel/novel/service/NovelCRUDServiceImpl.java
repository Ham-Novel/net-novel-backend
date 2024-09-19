package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NovelCRUDServiceImpl implements NovelCRUDService {
    private final NovelService novelService;
    private final NovelTagService novelTagService;

    public NovelCRUDServiceImpl(NovelService novelService, NovelTagService novelTagService) {
        this.novelService = novelService;
        this.novelTagService = novelTagService;
    }

    @Override
    public Long createNovel(NovelCreateDto novelCreateDto) {
        Long createdId = novelService.createNovel(novelCreateDto);

        List<NovelTagCreateDto> createDtoList = novelCreateDto.getTagNames().stream()
                .map(tagName -> new NovelTagCreateDto(createdId, tagName))
                .toList();

        novelTagService.createNovelTags(createDtoList);

        return createdId;
    }

    @Override
    public void updateNovel(NovelUpdateDto novelUpdateDto) {

    }

    @Override
    public void deleteNovel(NovelDeleteDto novelDeleteDto) {

    }
}
