package com.ham.netnovel.favoriteNovel.service;

import com.ham.netnovel.favoriteNovel.FavoriteNovel;
import com.ham.netnovel.favoriteNovel.FavoriteNovelId;
import com.ham.netnovel.favoriteNovel.FavoriteNovelRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Slf4j
class FavoriteNovelServiceImplTest {

    @Autowired
    FavoriteNovelRepository repository;

    @Autowired
    FavoriteNovelService service;

    @Test
    @Transactional
//    @Commit
    void test() {
        //given
        Boolean id = service.toggleFavoriteNovel("test1", 1L);
        log.info("id = " + id.toString());

        //when
        FavoriteNovel favoriteNovel = repository.findById(id).get();

        //then
        Assertions.assertThat(favoriteNovel.getNovel().getId()).isEqualTo(1L);
        Assertions.assertThat(favoriteNovel.getMember().getProviderId()).isEqualTo("test1");


        //delete
        service.toggleFavoriteNovel("test1", 1L);
        Optional<FavoriteNovel> record = repository.findById(id);
        Assertions.assertThat(record.isEmpty()).isTrue();
    }
}