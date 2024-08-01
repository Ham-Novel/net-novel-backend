package com.ham.netnovel.novelAverageRating.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelAverageRating.NovelAverageRating;

import java.math.BigDecimal;

public interface NovelAverageRatingService {

    /**
     * 특정 Novel의 평균 점수를 업데이트하는 메서드
     * @param novelId 평균 점수를 업데이트할 Novel 의 FK
     */

    void updateNovelAverageRating(Long novelId);


    /**
     * 별점 점수가 있는 모든 Novel의 평균 점수를 업데이트하는 메서드
     * 이 메서드는 스케줄러에 의해 특정 시간마다 실행
     */
    void updateAverageRatingForAllRatedNovels();


    /**
     * 새로운 NovelAverageRating 엔티티를 생성하여 DB에 저장하는 메서드
     * @param novel 평균 평점이 생성될 Novel 엔티티
     * @param averageValue 평균 별점
     * @param listSize 평균 평점을 계산하는 데 사용된 평점의 개수
     */
    void createNovelAverageRating( Novel novel,BigDecimal averageValue,int listSize);

    /**
     * NovelAverageRating 엔티티를 업데이트하는 메서드
     * @param novelAverageRating 업데이트할 NovelAverageRating 엔티티
     * @param averageValue 평균 별점
     * @param listSize 평균 평점을 계산하는 데 사용된 평점의 개수
     */
    void updateNovelAverageRating(NovelAverageRating novelAverageRating, BigDecimal averageValue, int listSize);


}
