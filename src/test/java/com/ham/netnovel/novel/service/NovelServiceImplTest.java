package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
@Slf4j
class NovelServiceImplTest {

    @Autowired
    NovelServiceImpl novelService;

    @Test
    void readTest() {
        // when
        Optional<Novel> novel = novelService.getNovel(1L);

        // then
        Assertions.assertThat(novel.isPresent()).isTrue();
    }

    @Test
    void createTest() {
        //given
        String authorId = "test2";
        NovelCreateDto createDto = NovelCreateDto.builder()
                .title("소설1")
                .description("Duis ea aliquip dolor sit dolore ut adipisicing eu tempor.")
                .accessorProviderId(authorId)
                .build();

        // when
        Long novelId = novelService.createNovel(createDto);

        // then
        Optional<Novel> novel = novelService.getNovel(novelId);
        Assertions.assertThat(novel.isPresent()).isTrue();
    }

    @Test
    void updateTest() {
        //given
        Long id = 1L;
        NovelUpdateDto updateDto = NovelUpdateDto.builder()
                .novelId(id)
                .accessorProviderId("test1")
                .title("변경된 작품 소개")
                .build();
//        log.info(updateDto.toString());

        //when
        novelService.updateNovel(updateDto);

        //then
        NovelInfoDto novelInfo = novelService.getNovelInfo(id);
        Assertions.assertThat(novelInfo.getTitle()).isEqualTo(updateDto.getTitle());
    }

    @Test
    void deleteNovel() {
        //given
        Long id = 1L;
        NovelDeleteDto deleteDto = NovelDeleteDto.builder()
                .novelId(id)
                .accessorProviderId("test1")
                .build();
//        log.info(deleteDto.toString());

        //when
        novelService.deleteNovel(deleteDto);

        //then
        Novel novel = novelService.getNovel(id)
                .orElseThrow(() -> new NoSuchElementException("에러"));
        Assertions.assertThat(novel.getStatus()).isEqualTo(NovelStatus.DELETED_BY_USER);
    }

    @Test
    void getMemberFavoriteNovels(){

        //테스트 providerId 입력
//        String providerId = "-";
        String providerId = "3333";

        List<Novel> favoriteNovels = novelService.getFavoriteNovels(providerId);

        if (favoriteNovels.isEmpty()){
            System.out.println("List 비었음");


        }else {

            for (Novel favoriteNovel : favoriteNovels) {
                System.out.println("소설정보");
                System.out.println(favoriteNovel.toString());
            }


        }




    }

    @Test
    void getBrowseNovels() {
        Pageable pageable = PageableUtil.createPageable(1, 5);
        List<NovelInfoDto> list = novelService.getNovelsRecent(pageable);
        Assertions.assertThat(list.isEmpty()).isFalse();
        list.forEach(novel -> {
//            log.info("id = {}, title = {}", novel.getId(), novel.getTitle());
        });
    }


    @Test
    public void getRatedNovelIds(){
        List<Long> ratedNovelIds = novelService.getRatedNovelIds();
        for (Long ratedNovelId : ratedNovelIds) {
            System.out.println("novelId ="+ratedNovelId);

        }

    }

    //테스트성공
    @Test
    public void getRankedNovels(){

//        String period = "daily";//테스트 성공
        String period = "weekly";//테스트 성공
//        String period = "monthly";//테스트 성공

        //페이지네이션 테스트 성공
        Pageable pageable = PageRequest.of(0, 3);
        List<NovelListDto> rankedNovels = novelService.getNovelsByRanking(period,pageable);

        if (rankedNovels.isEmpty()){
            System.out.println("비었음");
            return;
        }

        System.out.println("가져온 소설수 = "+rankedNovels.size());
        for (NovelListDto rankedNovel : rankedNovels) {
            System.out.println("********** 소설정보 **********");
            System.out.println(rankedNovel.toString());

        }

    }
}