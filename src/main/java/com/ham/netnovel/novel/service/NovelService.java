package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelDataDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;

import java.util.List;

public interface NovelService {

    List<Novel> getAllNovels();

    NovelDataDto getNovel(Long id);

    NovelDataDto createNovel(NovelCreateDto novelCreateDto);

    NovelDataDto updateNovel(NovelUpdateDto novelUpdateDto);

    NovelDataDto deleteNovel(NovelDeleteDto novelDeleteDto);
}
