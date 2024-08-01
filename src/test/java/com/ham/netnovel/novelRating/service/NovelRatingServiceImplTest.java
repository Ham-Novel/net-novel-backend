package com.ham.netnovel.novelRating.service;

import com.ham.netnovel.novelRating.dto.NovelRatingInfoDto;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NovelRatingServiceImplTest {


    private final NovelRatingService novelRatingService;


    @Autowired
    public NovelRatingServiceImplTest(NovelRatingService novelRatingService) {
        this.novelRatingService = novelRatingService;
    }



    @Test
    void saveNovelRating() {
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

    //테스트 성공
    @Test
    void getNovelRatingList(){
        //불러올 소설 Id
        Long novelId = 1L;

        List<NovelRatingInfoDto> novelRatingList = novelRatingService.getNovelRatingList(novelId);
        for (NovelRatingInfoDto novelRatingInfoDto : novelRatingList) {

            System.out.println(novelRatingInfoDto.toString());

        }

    }








    //NovelRating 테스트용 엔티티 생성 메서드
    //테스트 유저 엔티티로 별점 생성
    @Test
    void ratingTest(){
        String providerId = "test";
        Long novelId = 4L;


        for (int i=100; i<=200; i++){

            int randomNumber = (int) (Math.random() * 10) + 1; // 0부터 9까지의 숫자 생성 후 +1

            NovelRatingSaveDto build = NovelRatingSaveDto.builder()
                    .providerId(providerId+i)
                    .novelId(novelId)
                    .rating(randomNumber)
                    .build();

            novelRatingService.saveNovelRating(build);


        }






    }



}