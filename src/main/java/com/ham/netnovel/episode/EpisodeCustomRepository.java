package com.ham.netnovel.episode;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EpisodeCustomRepository {


    List<Episode> findEpisodesByConditions(String sortBy, Long novelId , Pageable pageable);

}
