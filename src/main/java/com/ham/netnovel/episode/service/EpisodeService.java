package com.ham.netnovel.episode.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeDataDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {

    EpisodeDataDto getEpisode(Long episodeId);
    EpisodeDataDto createEpisode(EpisodeCreateDto episodeCreateDto);
    EpisodeDataDto deleteEpisode(EpisodeUpdateDto episodeUpdateDto);
    EpisodeDataDto updateEpisode(EpisodeDeleteDto episodeDeleteDto);

    List<EpisodeDataDto> getEpisodesByNovel(Long novelId);
}
