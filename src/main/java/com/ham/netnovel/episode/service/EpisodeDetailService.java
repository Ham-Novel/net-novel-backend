package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.dto.EpisodeDetailDto;

public interface EpisodeDetailService {


    /**
     *
     * @param providerId
     * @param episodeId
     * @return
     */
    EpisodeDetailDto getEpisodeDetail(String providerId,Long episodeId);
}
