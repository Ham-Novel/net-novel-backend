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


    /**
     * novelId 값으로 DB에서 Novel 데이터 반환. 내부에서 Null 체크 수행.
     * @param novelId Novel의 PK값
     * @return NovelResponseDto
     */
    NovelResponseDto getNovel(Long novelId);

    //Novel Entity가 필요한 경우. Service 단에서만 사용.

    /**
     * novelId 값으로 DB에서 Novel 엔티티 반환. 외부에서 Null 체크 필요.
     * Service 계층에서 Novel 엔티티를 받아야하는 용도. 절대로 다른 계층에서 사용 금지.
     * @param novelId Novel의 PK값
     * @return Optional<Novel>
     */
    Optional<Novel> getNovelEntity(Long novelId);

    /**
     * 유저가 생성한 Novel을 DB 저장.
     * @param novelCreateDto
     * @return NovelResponseDto
     */
    NovelResponseDto createNovel(NovelCreateDto novelCreateDto);

    /**
     * DB에 저장된 Novel 데이터 변경.
     * @param novelUpdateDto
     * @return NovelResponseDto
     */
    NovelResponseDto updateNovel(NovelUpdateDto novelUpdateDto);

    /**
     * DB에 저장된 Novel 삭제.
     * @param novelDeleteDto
     * @return NovelResponseDto
     */
    NovelResponseDto deleteNovel(NovelDeleteDto novelDeleteDto);

    /**
     * 유저의 선호작 Novel 리스트 반환.
     * @param providerId 유저 PK 값.
     * @return List<Novel>
     */
    List<Novel> getFavoriteNovels(String providerId);
}
