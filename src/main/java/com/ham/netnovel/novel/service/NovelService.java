package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import com.ham.netnovel.novel.dto.NovelResponseDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;

import java.util.List;
import java.util.Optional;

public interface NovelService {

    List<Novel> getAllNovels();

    NovelResponseDto getNovel(Long id);

    //Novel Entity가 필요한 경우. Service 단에서만 사용.
    Optional<Novel> getNovelEntity(Long id);

    NovelResponseDto createNovel(NovelCreateDto novelCreateDto);

    NovelResponseDto updateNovel(NovelUpdateDto novelUpdateDto);

    NovelResponseDto deleteNovel(NovelDeleteDto novelDeleteDto);

    List<Novel> getFavoriteNovels(String providerId);

    /**
     * 별점 점수가 있는 Novel의 id값들을 List로 반환하는 메서드
     * @return List Long 타입으로 반환
     */
    List<Long> getRatedNovelIds();


}
