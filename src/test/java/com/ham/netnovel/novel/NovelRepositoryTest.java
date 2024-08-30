package com.ham.netnovel.novel;

import com.ham.netnovel.common.utils.PageableUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
@Slf4j
class NovelRepositoryTest {

    @Autowired
    NovelRepository novelRepository;

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
        System.out.println(">>Load Novel Model: " + loadNovel.toString());
        Assertions.assertThat(loadNovel.getTitle()).isEqualTo(newNovel.getTitle());
        Assertions.assertThat(loadNovel.getDescription()).isEqualTo(newNovel.getDescription());

    }

    @Test
    void queryTest() {
        Pageable pageable = PageableUtil.createPageable(0, 5);
        List<Novel> list = novelRepository.findByLatestEpisodes(pageable);
        Assertions.assertThat(list.isEmpty()).isFalse();
//        list.forEach(novel -> {
//            log.info("id = {}, title = {}", novel.getId(), novel.getTitle());
//        });
    }

}