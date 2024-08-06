package com.ham.netnovel.episode.service;


import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.service.CoinCostPolicyService;
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
import org.springframework.data.domain.Pageable;

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
    private final CoinCostPolicyService costPolicyService;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, NovelService novelService, CoinCostPolicyService costPolicyService) {
        this.episodeRepository = episodeRepository;
        this.novelService = novelService;
        this.costPolicyService = costPolicyService;
    }

    @Override
    @Transactional(readOnly = true)//읽기전용
    public Optional<Episode> getEpisode(Long episodeId) {
        return episodeRepository.findById(episodeId);
    }

    @Override
    @Transactional
    public void createEpisode(EpisodeCreateDto episodeCreateDto) {
        Novel novelProperty = novelService.getNovel(episodeCreateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("Novel 정보 없음"));

        CoinCostPolicy costPolicyProperty = costPolicyService.getPolicyEntity(episodeCreateDto.getCostPolicyId())
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy 정보 없음"));

        try {
            Episode targetRecord = Episode.builder()
                    .title(episodeCreateDto.getTitle())
                    .content(episodeCreateDto.getContent())
                    .costPolicy(costPolicyProperty)
                    .novel(novelProperty)
                    .chapter(novelProperty.getEpisodes().size()+1) //자동으로 넘버링 증가
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
            String updateTitle = (episodeUpdateDto.getTitle() != null)
                    ? episodeUpdateDto.getTitle() : targetRecord.getTitle();
            String updateContent = (episodeUpdateDto.getContent() != null)
                    ? episodeUpdateDto.getContent() : targetRecord.getContent();
            CoinCostPolicy updateCostPolicy = (episodeUpdateDto.getCostPolicyId() != null)
                    ? costPolicyService.getPolicyEntity(episodeUpdateDto.getCostPolicyId())
                    .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy 정보 없음"))
                    : targetRecord.getCostPolicy();

            targetRecord.updateEpisode(updateTitle, updateContent, updateCostPolicy);
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
    @Transactional(readOnly = true)
    public List<EpisodeListItemDto> getNovelEpisodes(Long novelId) {
        try {
            return episodeRepository.findByNovel(novelId)
                    .stream()
                    .map(this::convertEntityToListDto) //Episode => EpisodeListItemDto 변환
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("getEpisodesByNovel 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EpisodeListItemDto> getNovelEpisodesByRecent(Long novelId, Pageable pageable) {
        try {
            return episodeRepository.findByNovelOrderByCreatedAtDesc(novelId, pageable)
                    .stream()
                    .map(this::convertEntityToListDto) //Episode => EpisodeListItemDto 변환
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("getEpisodesByNovel 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EpisodeListItemDto> getNovelEpisodesByInitial(Long novelId, Pageable pageable) {
        try {
            return episodeRepository.findByNovelOrderByCreatedAtAsc(novelId, pageable)
                    .stream()
                    .map(this::convertEntityToListDto) //Episode => EpisodeListItemDto 변환
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("getEpisodesByNovel 메서드 에러 발생", ex.getCause());
        }
    }

    /**
     *  Episode Entity => EpisodeListItemDto 변환 메서드
     * @param episode 에피소드 엔티티
     * @return EpisodeListItemDto
     */
    private EpisodeListItemDto convertEntityToListDto(Episode episode) {
        return EpisodeListItemDto.builder()
                .episodeId(episode.getId())
                .chapter(episode.getChapter())
                .title(episode.getTitle())
                .views(episode.getView())
                .letterCount(episode.getContent().length())
                .commentCount(episode.getComments().size())
                .uploadDate(episode.getCreatedAt())
                .coinCost(episode.getCostPolicy().getCoinCost())
                .build();
    }
}
