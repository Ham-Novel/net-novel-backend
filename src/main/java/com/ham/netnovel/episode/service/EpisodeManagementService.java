package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import com.ham.netnovel.episodeViewCount.ViewCountIncreaseDto;

import java.util.List;
import java.util.Map;

public interface EpisodeManagementService {

    //****** 에피소드의 기본적인 CRUD를 제외한 비즈니스 로직을 생성하는 서비스 계층 ******


    /**
     * 에피소드 상세 정보를 전송하는 메서드
     *
     * 유저의 에피소드 결제 내역이 존재할경우 에피소드 전송
     * 문제 없을경우, Redis에 에피소드 조회수 기록 1 증가시킴
     * @param providerId 에피소드 조회를 요청한 유저
     * @param episodeId 유저가 요청한 에피소드 ID
     * @return EpisodeDetailDto 에피소드 상세정보
     */
    EpisodeDetailDto getEpisodeDetail(String providerId,Long episodeId);

    /**
     * Redis 에 저장된 에피소드 조회수 정보를 DB에 업데이트 하는 메서드,
     * Episode 엔티티의 view 컬럼과 EpisodeViewCount 엔티티 업데이트,
     * 스케줄러로 일정시간마다 실행,
     * 갱신에 문제가 없을경우, Redis 데이터 초기화
     */
    void updateEpisodeViewCountFromRedis();

    /**
     * Episode 엔티티의 view 컬럼 필드값을 업데이트하는 메서드
     * EpisodeViewCount 엔티티 업데이트시 트랜잭션으로 묶어서 실행, DB 무결성 훼손 방지
     * @param episodes 에피소드 엔티티 List
     * @param viewCountIncreaseDtos 에피소드 Id와 에피소드별 증가시킬 조회수를 담은 DTO
     * @return boolean DB 업데이트 성공시 true 반환
     */
    boolean updateEpisodeEntityViewColumn(Map<Long, Episode> episodes, List<ViewCountIncreaseDto> viewCountIncreaseDtos);
}
