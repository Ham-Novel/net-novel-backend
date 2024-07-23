package com.ham.netnovel.novel.service;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;

import java.util.List;
import java.util.Optional;

public interface NovelService {

    List<Novel> getAllNovels();

    Novel getNovel(Long id);

    Novel createNovel(NovelCreateDto novelCreateDto);

    Novel updateNovel(NovelUpdateDto novelUpdateDto);

    Novel deleteNovel(NovelDeleteDto novelDeleteDto);
}
