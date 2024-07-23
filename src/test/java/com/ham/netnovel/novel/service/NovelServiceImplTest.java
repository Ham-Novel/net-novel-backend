package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.NovelStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NovelServiceImplTest {
    @Autowired
    NovelRepository novelRepository;

    @Autowired
    NovelServiceImpl novelService;

    @Test
    void readTest() {
        //given
        Novel newNovel = Novel.builder()
                .title("전형적인 빙의물")
                .description("그냥 평범한 판타지 빙의물입니다.")
                .build();
        System.out.println(">> Novel Model: " + newNovel.toString());

        // when
        novelRepository.save(newNovel);

        // then
        Optional<Novel> loadNovel = novelService.getNovel(1L);

        if (loadNovel.isPresent()) {
            System.out.println(">> Novel Model: " + loadNovel.toString());
        }
    }
}