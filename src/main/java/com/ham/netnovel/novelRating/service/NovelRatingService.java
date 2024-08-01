package com.ham.netnovel.novelRating.service;

import com.ham.netnovel.novelRating.dto.NovelRatingInfoDto;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;

import java.util.List;

public interface NovelRatingService {

    /**
     * 유저가 소설(Novel)에 별점을 등록했을때 이를 DB에 저장하는 메서드
     * @param novelRatingSaveDto 유저정보, 소설정보, 별점점수를 저장하는 메서드
     */
    void saveNovelRating(NovelRatingSaveDto novelRatingSaveDto);


    /**
     * 주어진 소설 ID에 대한 별점 정보를 조회하여 리스트로 반환하는 메서드
     * @param novelId Novel 엔티티 primary key
     * @return List NovelRatingInfoDto 형태의 List 반환
     */
    List<NovelRatingInfoDto> getNovelRatingList(Long novelId);







}
