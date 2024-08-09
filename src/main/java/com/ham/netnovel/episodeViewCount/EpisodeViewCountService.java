package com.ham.netnovel.episodeViewCount;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;

import java.time.LocalDate;
import java.util.List;

public interface EpisodeViewCountService {

    /**
     * 에피소드의 오늘 날짜 조회수를 증가시키는 메서드
     * @param episode 조회수를 증가시킬 Episode 엔티티. null 체크 후 메서드 호출 필요
     */
    void increaseViewCount(Episode episode);


    /**
     * 특정 날짜의 소설 조회수로 랭킹을 계산해 반환하는 메서드
     * @param todayDate 계산할 날짜
     * @return List Novel 엔티티와 totalViews(총조회수) 를 DTO에 담아 List 형태로 반환
     */
    List<NovelRankingUpdateDto> getDaliyRanking(LocalDate todayDate);

    /**
     * 한주동안의 소설 조회수로 랭킹을 계산해 반환하는 메서드
     * 파라미터로 입력되는 날짜 기준으로 7일전 ~ 1일전 까지의 조회수 계산
     * @param todayDate 계산할 날짜
     * @return List Novel 엔티티와 totalViews(총조회수) 를 DTO에 담아 List 형태로 반환
     */
    List<NovelRankingUpdateDto>  getWeeklyRanking(LocalDate todayDate);

}
