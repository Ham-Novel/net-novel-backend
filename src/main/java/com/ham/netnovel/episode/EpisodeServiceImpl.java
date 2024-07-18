package com.ham.netnovel.episode;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class EpisodeServiceImpl implements EpisodeService {


    private final EpisodeRepository episodeRepository;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }


    //다른 메서드에서 사용시 Null체크 필수!!
    @Override
    @Transactional(readOnly = true)//읽기전용
    public Optional<Episode> getEpisode(Long episodeId) {
       return episodeRepository.findById(episodeId);
       

    }
}
