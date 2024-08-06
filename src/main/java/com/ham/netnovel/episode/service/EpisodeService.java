package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {

    /**
     * episodeId 값으로 DB에서 Entity 가져오는 메서드. 사용 시 Null 체크 필수.
     * @return Optional<Episode>
     */
    Optional<Episode> getEpisode(Long episodeId);

    /**
     * Episode를 DB에 생성하는 메서드
     */
    void createEpisode(EpisodeCreateDto episodeCreateDto);

    /**
     * DB에 저장된 Episode 프로퍼티를 수정하는 메서드
     */
    void updateEpisode(EpisodeUpdateDto episodeUpdateDto);

    /**
     * DB에 저장된 Episode 삭제하는 메서드
     */
    void deleteEpisode(EpisodeDeleteDto episodeDeleteDto);

    /**
     * 해당 Novel에 속한 모든 Episodes List 데이터를 가져오는 메서드
     * @param novelId 소설 id
     * @return List<EpisodeDataDto>
     */
    List<EpisodeListItemDto> getNovelEpisodes(Long novelId);

    /**
     * 해당 Novel에 속한 모든 Episodes List dto를 최신순으로 가져오는 메서드.
     * 페이지 단위로 일부만 가져옴.
     * @param novelId 소설 id
     * @param pageable 페이지 정보.
     * @return List<EpisodeDataDto>
     */
    List<EpisodeListItemDto> getNovelEpisodesByRecent(Long novelId, Pageable pageable);

    /**
     * 해당 Novel에 속한 모든 Episodes List dto를 최초순으로 가져오는 메서드.
     * 페이지 단위로 일부만 가져옴.
     * @param novelId 소설 id
     * @param pageable 페이지 정보.
     * @return List<EpisodeDataDto>
     */
    List<EpisodeListItemDto> getNovelEpisodesByInitial(Long novelId, Pageable pageable);
}
