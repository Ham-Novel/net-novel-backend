package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;

import java.util.List;

public interface NovelService {

    List<Novel> getAllNovels();

    Novel getNovel(Long id);

    Novel createNovel(NovelCreateDto novelCreateDto);

    Novel updateNovel(NovelUpdateDto novelUpdateDto);

    Novel deleteNovel(NovelDeleteDto novelDeleteDto);


    List<Novel> getFavoriteNovels(String providerId);




}
