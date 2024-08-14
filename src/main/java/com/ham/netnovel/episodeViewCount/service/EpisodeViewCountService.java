package com.ham.netnovel.episodeViewCount.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episodeViewCount.ViewCountIncreaseDto;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EpisodeViewCountService {

    /**
     * Episode 엔티티와 조회수 정보로, EpisodeViewCount 엔티티의 조회수를 갱신하는 메서드
     * @param episodes 조회수가 증가한 Episode 엔티티 List
     * @param viewCountIncreaseDtos Redis에 저장되어있던 episodeId 와 조회수 값을 담은 DTO
     */
    void updateEpisodeViewCountEntity(Map<Long, Episode> episodes, List<ViewCountIncreaseDto> viewCountIncreaseDtos);



    /**
     * 특정 날짜의 소설 조회수로 랭킹을 계산해 반환하는 메서드
     * @param todayDate 계산할 날짜
     * @return List Novel 엔티티와 totalViews(총조회수) 를 DTO에 담아 List 형태로 반환
     */
    List<NovelRankingUpdateDto> getDailyRanking(LocalDate todayDate);

    /**
     * 한주동안의 소설 조회수로 랭킹을 계산해 반환하는 메서드
     * 파라미터로 입력되는 날짜 기준으로 7일전 ~ 1일전 까지의 조회수 계산
     * @param todayDate 계산할 날짜
     * @return List Novel 엔티티와 totalViews(총조회수) 를 DTO에 담아 List 형태로 반환
     */
    List<NovelRankingUpdateDto>  getWeeklyRanking(LocalDate todayDate);


    /**
     * 특정 에피소드의 조회수를 1 올리는 메서드, 데이터는 Redis 에 저장
     * @param episodeId 에피소드의 ID
     */
    void incrementEpisodeViewCountInRedis(Long episodeId);


    /**
     * Redis 에서 에피소드 조회수 기록을 받아와 DTO List 로 반환하는 메서드
     * 데이터는 Hash 형태로 key 값은 episodeId, value 는 조회수
     * @return List<ViewCountIncreaseDto> episodeId 와 조회수를 담는 ViewCountIncreaseDto 형태의 객체 List 반환
     */
    List<ViewCountIncreaseDto>  getEpisodeViewCountFromRedis();



}
