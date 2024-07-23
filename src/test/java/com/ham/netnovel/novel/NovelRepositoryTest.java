package com.ham.netnovel.novel;

import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.Service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NovelRepositoryTest {

    @Autowired
    NovelRepository novelRepository;
//
//    @Autowired
//    MemberService memberService;

    @Test
    void basicCreate() {
        //given
        Novel newNovel = Novel.builder()
                .title("전형적인 빙의물")
                .description("그냥 평범한 판타지 빙의물입니다.")
                .build();

        System.out.println(">>Create Novel Model: " + newNovel.toString());


        // when
        novelRepository.save(newNovel);

        // then
        Novel loadNovel = novelRepository.findById(1L).orElseThrow(() -> new NullPointerException());
//        System.out.println(">>Load Novel Model: " + loadNovel.toString());
        Assertions.assertThat(loadNovel.getTitle()).isEqualTo(newNovel.getTitle());
        Assertions.assertThat(loadNovel.getDescription()).isEqualTo(newNovel.getDescription());

    }

}