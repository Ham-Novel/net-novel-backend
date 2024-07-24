package com.ham.netnovel.episode.service;


import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeDataDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

//ToDo 에피소드 생성, 삭제, 이동 시 episodeNumber 자동 넘버링 및 재정렬 로직 구현
@Service
@Slf4j
public class EpisodeServiceImpl implements EpisodeService {


    private final EpisodeRepository episodeRepository;
    private final NovelService novelService;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, NovelService novelService) {
        this.episodeRepository = episodeRepository;
        this.novelService = novelService;
    }

    @Override
    @Transactional(readOnly = true)//읽기전용
    public EpisodeDataDto getEpisode(Long episodeId) {
        return episodeRepository.findById(episodeId)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 Episode입니다."))
                .parseDataDto();
    }

    @Override
    public Optional<Episode> getEpisodeEntity(Long episodeId) {
        return episodeRepository.findById(episodeId);
    }

    @Override
    @Transactional
    public EpisodeDataDto createEpisode(EpisodeCreateDto episodeCreateDto) {
        Novel novelFrom = novelService.getNovelEntity(episodeCreateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        Episode targetEpisode = Episode.builder()
                .title(episodeCreateDto.getTitle())
                .content(episodeCreateDto.getContent())
                .novel(novelFrom)
                .episodeNumber(novelFrom.getEpisodes().size()+1) //자동으로 넘버링 증가
                .build();
        return episodeRepository.save(targetEpisode).parseDataDto();
    }

    @Override
    @Transactional
    public EpisodeDataDto deleteEpisode(EpisodeUpdateDto episodeUpdateDto) {
        Episode targetEpisode = episodeRepository.findById(episodeUpdateDto.getEpisodeId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Episode입니다."));

        targetEpisode.updateEpisode(episodeUpdateDto.getTitle(), episodeUpdateDto.getContent());
        return episodeRepository.save(targetEpisode).parseDataDto();
    }

    @Override
    @Transactional
    public EpisodeDataDto updateEpisode(EpisodeDeleteDto episodeDeleteDto) {
        Episode targetEpisode = episodeRepository.findById(episodeDeleteDto.getEpisodeId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Episode입니다."));

        episodeRepository.deleteById(episodeDeleteDto.getEpisodeId());

        return targetEpisode.parseDataDto();
    }

    @Override
    @Transactional
    public List<EpisodeDataDto> getEpisodesByNovel(Long novelId) {
        return episodeRepository.findByNovel(novelId)
                .stream()
                .map(episode -> episode.parseDataDto())
                .collect(Collectors.toList());
    }
}
