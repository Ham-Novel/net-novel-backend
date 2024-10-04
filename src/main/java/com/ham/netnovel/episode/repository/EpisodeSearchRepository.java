package com.ham.netnovel.episode.repository;

import com.ham.netnovel.episode.Episode;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EpisodeSearchRepository {


    List<Episode> findEpisodesByConditions(String sortBy, Long novelId , Pageable pageable);

}
