package com.ham.netnovel.novelRating.service;

import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NovelRatingServiceImplTest {


    private final NovelRatingService novelRatingService;


    @Autowired
    public NovelRatingServiceImplTest(NovelRatingService novelRatingService) {
        this.novelRatingService = novelRatingService;
    }



    @Test
    void saveEpisodeRating() {
        //테스트 계정
        String providerId = "test";

        //존재하지 않는 유저 테스트 NoSuchElementException 던져짐
//        String providerId = "testest";

        Long novelId = 1L;
        
        //존재하지 않는 에피소드 테스트 NoSuchElementException 던져짐
//        Long novelId = 231033L;

        //별점
        Integer rating = 10;
        //범위 테스트 IllegalArgumentException 던져짐
//        Integer rating = 20;
        //null 테스트 IllegalArgumentException 던져짐
//        Integer rating = null;


        NovelRatingSaveDto build = NovelRatingSaveDto.builder()
                .providerId(providerId)
                .novelId(novelId)
                .rating(rating)
                .build();

        novelRatingService.saveNovelRating(build);
    }
}