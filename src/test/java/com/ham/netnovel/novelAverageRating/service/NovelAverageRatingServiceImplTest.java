package com.ham.netnovel.novelAverageRating.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class NovelAverageRatingServiceImplTest {

   private final NovelAverageRatingService novelAverageRatingService;

   @Autowired
    NovelAverageRatingServiceImplTest(NovelAverageRatingService novelAverageRatingService) {
        this.novelAverageRatingService = novelAverageRatingService;
    }

    //테스트 성공
    @Test
    void updateNovelAverageRating() {
       //평균 별점을 업데이트할 novelId
       Long novelId = 1L;

       //
       novelAverageRatingService.updateNovelAverageRating(novelId);
    }


    //테스트 성공
    @Test
    void updateAverageRatingForAllRatedNovels(){

       //별점이 기록된 novel 엔티티들의 평균 별점 최신화
       novelAverageRatingService.updateAverageRatingForAllRatedNovels();


   }

}