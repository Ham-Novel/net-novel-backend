package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.EpisodeStatus;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;


@SpringBootTest
@Slf4j
class EpisodeServiceImplTest {

    @Autowired
    EpisodeRepository repository;

    @Autowired
    EpisodeService service;

    @Autowired
    JdbcTemplate jdbcTemplate;

    void setup() {
        //DB records 전부 삭제
        repository.deleteAll();

        //auto_increment id를 1부터 초기화.
        String sql = "ALTER TABLE episode ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.execute(sql);
    }

    @Test
    void create() {
        //given
        setup();
        EpisodeCreateDto createDto = EpisodeCreateDto.builder()
                .novelId(1L)
                .title("에피소드 첫번째")
                .content("Cillum consequat eiusmod consequat anim est quis ullamco fugiat ullamco veniam mollit cupidatat. Eiusmod fugiat fugiat officia culpa aliqua ut dolor excepteur tempor irure quis dolor. Lorem in non pariatur laboris sunt aliquip ex exercitation. Ipsum voluptate enim commodo anim dolore magna ea pariatur amet eu.\n")
                .costPolicyId(1L)
                .build();
        //when
        service.createEpisode(createDto);
        log.info(createDto.toString());

        //then
        Episode getEntity = service.getEpisode(1L)
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

        Assertions.assertThat(getEntity.getTitle()).isEqualTo(createDto.getTitle());
        Assertions.assertThat(getEntity.getContent()).isEqualTo(createDto.getContent());
    }

    @Test
    void update() {
        //given
        EpisodeUpdateDto updateDto = EpisodeUpdateDto.builder()
                .episodeId(1L)
                .title("에피소드 이름 변경됨")
                .build();

        //when
        service.updateEpisode(updateDto);

        //then
        Episode getEntity = service.getEpisode(1L)
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

        Assertions.assertThat(getEntity.getTitle()).isEqualTo(updateDto.getTitle());
    }

    @Test
    void delete() {
        //given
        EpisodeDeleteDto deleteDto = EpisodeDeleteDto.builder()
                .episodeId(1L)
                .build();

        //when
        service.deleteEpisode(deleteDto);

        //then
        Episode getEntity = service.getEpisode(1L)
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));
        Assertions.assertThat(getEntity.getStatus()).isEqualTo(EpisodeStatus.DELETED_BY_USER);
    }


    @Test
    void getListItem() {
        //given

        //when

        //then
    }
}