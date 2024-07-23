package com.ham.netnovel.novel.service;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NovelServiceImplTest {
    @Autowired
    NovelRepository novelRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    NovelServiceImpl novelService;

    @Test
    void readTest() {
        //given
        Member member = Member.builder()
                .providerId("1111")
                .build();

        Novel newNovel = Novel.builder()
                .title("전형적인 빙의물")
                .description("그냥 평범한 판타지 빙의물입니다.")
                .build();
        System.out.println(">> Novel Model: " + newNovel.toString());

        // when
        memberRepository.save(member);
        novelRepository.save(newNovel);

        // then
        Novel loadNovel = novelService.getNovel(1L);
        System.out.println(">> Novel Model: " + loadNovel.toString());
    }

    @Test
    void createTest() {
        //given
        NovelCreateDto novelCreateDto = NovelCreateDto.builder()
                .title("전형적인 빙의물")
                .description("그냥 평범한 판타지 빙의물입니다.")
                .authorProviderId("1111")
                .build();
        System.out.println(">> Novel Create " + novelCreateDto.toString());

        // when
        Novel novel = novelService.createNovel(novelCreateDto);

        // then
        System.out.println(">> Novel Model: " + novel.toString());
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
}