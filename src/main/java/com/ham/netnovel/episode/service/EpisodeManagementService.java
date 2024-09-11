package com.ham.netnovel.episode.service;

import com.ham.netnovel.common.exception.EpisodeNotPurchasedException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.data.IndexDirection;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import com.ham.netnovel.episodeViewCount.ViewCountIncreaseDto;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface EpisodeManagementService {

    //****** 에피소드의 기본적인 CRUD를 제외한 비즈니스 로직을 생성하는 서비스 계층 ******


    /**
     * 지정된 에피소드의 상세 정보를 반환합니다.
     *
     * <ul>
     *     <li>에피소드가 존재하는지 확인하고, 존재하지 않으면 {@link NoSuchElementException}을 발생시킵니다.</li>
     *     <li>코인 비용을 검증하고, 유효하지 않으면 {@link IllegalArgumentException}을 발생시킵니다.</li>
     *     <li>유료 에피소드일 경우 사용자의 결제 내역을 확인하고, 결제 내역이 없으면 {@link EpisodeNotPurchasedException}을 발생시킵니다.</li>
     *     <li>레디스에서 에피소드 조회수를 1 증가시킵니다.</li>
     *     <li>{@link EpisodeDetailDto} 객체로 에피소드 정보를 반환합니다.</li>
     * </ul>
     *
     * @param providerId 요청자의 ID
     * @param episodeId 조회할 에피소드의 ID
     * @return {@link EpisodeDetailDto} 에피소드 상세 정보
     * @throws NoSuchElementException 에피소드가 존재하지 않는 경우
     * @throws EpisodeNotPurchasedException 유료 에피소드에 대한 결제 내역이 없는 경우
     * @throws IllegalArgumentException 코인 비용이 유효하지 않은 경우
     */
    EpisodeDetailDto getEpisodeDetail(String providerId,Long episodeId);



    /**
     * 무료 에피소드의 상세 정보를 반환합니다.
     *
     * <p>에피소드가 존재하는지 확인하고, 존재하지 않으면 {@link NoSuchElementException}을 발생시킵니다.</p>
     * <p>에피소드가 존재하면 레디스에서 에피소드 조회수를 1 증가시킨 후
     * {@link EpisodeDetailDto} 객체로 에피소드 정보를 반환합니다.</p>
     *
     * @param episodeId 조회할 에피소드의 ID
     * @return {@link EpisodeDetailDto} 에피소드 상세 정보
     * @throws NoSuchElementException 에피소드가 존재하지 않는 경우
     * @throws IllegalArgumentException 코인 비용이 유효하지 않은 경우
     */
    EpisodeDetailDto getFreeEpisodeDetail(Long episodeId);


    /**
     * 해당 에피소드의 바로 다음 chapter인 에피소드 id를 반환하는 메서드
     *
     *
     */
    EpisodeDetailDto getBesideEpisode(String providerId, Long episodeId, IndexDirection direction);

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
