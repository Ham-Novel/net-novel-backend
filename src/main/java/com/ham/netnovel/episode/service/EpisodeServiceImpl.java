package com.ham.netnovel.episode.service;


import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.EpisodeStatus;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.dto.EpisodeDeleteDto;
import com.ham.netnovel.episode.dto.EpisodeUpdateDto;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<Episode> getEpisodeEntity(Long episodeId) {
        return episodeRepository.findById(episodeId);
    }

    @Override
    @Transactional
    public void createEpisode(EpisodeCreateDto episodeCreateDto) {
        Novel novelData = novelService.getNovelEntity(episodeCreateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("Novel 정보 없음"));

        try {
            Episode targetRecord = Episode.builder()
                    .title(episodeCreateDto.getTitle())
                    .content(episodeCreateDto.getContent())
                    .novel(novelData)
                    .chapter(novelData.getEpisodes().size()+1) //자동으로 넘버링 증가
                    .build();
            episodeRepository.save(targetRecord);
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("createEpisode 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional
    public void updateEpisode(EpisodeUpdateDto episodeUpdateDto) {
        Episode targetRecord = episodeRepository.findById(episodeUpdateDto.getEpisodeId())
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

        try {
            //episodeUpdateDto에서 null값은 기존값 할당. 변경할 값만 입력하면 됨.
            String updateTitle = (episodeUpdateDto.getTitle() != null) ? episodeUpdateDto.getTitle() : targetRecord.getTitle();
            String updateContent = (episodeUpdateDto.getContent() != null) ? episodeUpdateDto.getContent() : targetRecord.getContent();

            targetRecord.updateEpisode(updateTitle, updateContent);
            episodeRepository.save(targetRecord);
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("updateEpisode 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteEpisode(EpisodeDeleteDto episodeDeleteDto) {
        try {
            Episode targetRecord = episodeRepository.findById(episodeDeleteDto.getEpisodeId())
                    .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

            targetRecord.changeStatus(EpisodeStatus.DELETED_BY_USER);
            episodeRepository.save(targetRecord);
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("deleteEpisode 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional
    public List<EpisodeListItemDto> getEpisodesByNovel(Long novelId) {
        try {
            return episodeRepository.findByNovel(novelId)
                    .stream()
                    //Episode => EpisodeListItemDto 변환
                    .map((episode -> EpisodeListItemDto.builder()
                            .episodeId(episode.getId())
                            .chapter(episode.getChapter())
                            .title(episode.getTitle())
                            .views(episode.getView())
                            .letterCount(episode.getContent().length())
                            .commentCount(episode.getComments().size())
                            .uploadDate(episode.getCreatedAt())
                            .build()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("getEpisodesByNovel 메서드 에러 발생", ex.getCause());
        }
    }
}
