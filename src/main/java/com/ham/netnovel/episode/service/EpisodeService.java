package com.ham.netnovel.episode.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface EpisodeService {

    /**
     * episodeId 값으로 DB에서 Entity 가져오는 메서드. 사용 시 Null 체크 필수.
     * @return Optional<Episode>
     */
    Optional<Episode> getEpisode(Long episodeId);

    /**
     * episodeId List로 DB에서 엔티티 List를 가져오는 메서드
     * 사용시 null 체크 필수
     * @param episodeIds episodeId 리스트
     * @return List<Episode>
     */
    List<Episode> getEpisodeList(List<Long> episodeIds);

    /**
     * 새로운 에피소드를 생성합니다.
     *
     * <p>이 메서드는 제공된 {@link  EpisodeCreateDto}를 기반으로 새로운 에피소드를 생성합니다.</p>
     *
     * <p>먼저 에피소드 생성 요청자(제공자)가 소설의 저자와 일치하는지 확인합니다. 요청자와 저자가 일치하지 않거나
     * 소설이 존재하지 않는 경우 예외가 발생합니다. </p>
     * <p>또한, 제공된 {@code CostPolicyId}에 해당하는
     * {@code CoinCostPolicy} 정보가 없으면 예외로 던집니다..</p>
     *
     * <p>에피소드 엔티티가 성공적으로 생성되면, 데이터베이스에 저장하고 해당 소설이 업데이트되었다는
     * 메시지를 Redis를 통해 발송합니다. 예외가 발생할 경우, {@code ServiceMethodException} 예외가 던져집니다.</p>
     *
     * @param episodeCreateDto 새로운 에피소드의 정보를 담고 있는 {@link  EpisodeCreateDto} 객체
     * @throws IllegalArgumentException 유효하지 않은 소설 정보이거나 요청자 정보가 올바르지 않은 경우
     * @throws NoSuchElementException 제공된 {@code CostPolicyId}에 대한 {@code CoinCostPolicy} 정보가 없는 경우
     * @throws ServiceMethodException 데이터베이스 작업 중 예외가 발생한 경우
     */
    void createEpisode(EpisodeCreateDto episodeCreateDto);

    /**
     * DB에 저장된 Episode 프로퍼티를 수정하는 메서드
     */
    void updateEpisode(EpisodeUpdateDto episodeUpdateDto);

    /**
     * 에피소드 상태를 삭제상태로 변경하는 메서드 입니다.
     *
     * <p>주어진 {@link  EpisodeDeleteDto}를 바탕으로 에피소드를 삭제합니다. </p>
     * <p>먼저, 데이터베이스에서 해당 에피소드를 조회하고,
     * 에피소드의 소속 소설의 작가 정보와 삭제 요청자의 정보가 일치하는지 확인합니다.
     * 일치하지 않거나 에피소드 정보가 존재하지 않는 경우 예외로 던집니다.</p>
     * <p>이후, 에피소드의 상태를{@code DELETED_BY_USER}로 변경하고,
     * 변경된 엔티티를 데이터베이스에 저장합니다.</p>
     *
     * @param episodeDeleteDto 에피소드 삭제 정보를 담고 있는 {@link  EpisodeDeleteDto} 객체
     * @throws NoSuchElementException 에피소드 정보가 존재하지 않거나 요청자와 작가 정보가 일치하지 않는 경우
     * @throws ServiceMethodException 데이터베이스 작업 중 예외가 발생한 경우
     */
    void deleteEpisode(EpisodeDeleteDto episodeDeleteDto);


    /**
     * 해당 Novel에 속한 모든 Episodes List의 메타 데이터를 가져오는 메서드
     * @param novelId 소설 id
     * @return EpisodeListInfoDto
     */
    EpisodeListInfoDto getNovelEpisodesInfo(Long novelId);

    /**
     * 소설 ID와 필터 조건에 따라 에피소드 목록을 조회하는 메서드 입니다.
     *
     * <p> 이 메서드는 에피소드 검색 리포지토리에서 주어진 필터를 적용하여 결과를 가져온 뒤,
     * 각 에피소드 엔티티를 {@link EpisodeListItemDto}로 변환하여 반환합니다.</p>
     *
     * @param sortBy 정렬 기준
     * @param novelId 소설의 고유 ID
     * @param pageable 페이지네이션 정보를 포함한 객체
     * @return  에피소드 목록을 나타내는 {@link EpisodeListItemDto} 리스트
     * @throws ServiceMethodException 메서드 실행 중 예외가 발생한 경우
     */
    List<EpisodeListItemDto> getEpisodesByConditions(String sortBy, Long novelId, Pageable pageable);
}
