package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {

    /**
     * episodeId 값으로 DB에서 Entity 가져오는 메서드. 사용 시 Null 체크 필수.
     * @return Optional<Episode>
     */
    Optional<Episode> getEpisodeEntity(Long episodeId);

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
     * @return List<EpisodeDataDto>
     */
    List<EpisodeListItemDto> getEpisodesByNovel(Long novelId);
}
