package com.ham.netnovel.episode.service;


import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.service.CoinCostPolicyService;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.message.RedisMessagePublisher;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.data.EpisodeStatus;
import com.ham.netnovel.episode.dto.*;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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

    private final RedisMessagePublisher redisMessagePublisher;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, NovelService novelService, CoinCostPolicyService costPolicyService, RedisMessagePublisher redisMessagePublisher) {
        this.episodeRepository = episodeRepository;
        this.novelService = novelService;
        this.costPolicyService = costPolicyService;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @Override
    @Transactional(readOnly = true)//읽기전용
    public Optional<Episode> getEpisode(Long episodeId) {
        return episodeRepository.findById(episodeId);
    }

    @Override
    public List<Episode> getEpisodeList(List<Long> episodeIds) {
        return episodeRepository.findAllById(episodeIds);
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
                    .chapter(novelProperty.getEpisodes().size() + 1) //자동으로 넘버링 증가
                    .build();
            Episode save = episodeRepository.save(targetRecord);

            //Redis로 Novel이 업데이트 되었다는 메시지를 송부
            publishUpdateMessage(
                    novelProperty.getId(),
                    save.getId(),
                    novelProperty.getTitle(),
                    episodeCreateDto.getTitle(),
                    novelProperty.getThumbnailFileName());

        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("createEpisode 메서드 에러 발생", ex.getCause());
        }
    }

    /*
   Redis를 사용하여 소설 업데이트 메시지를 발송하는 메서드입니다.

   이 메서드는 소설과 에피소드의 업데이트 정보를 Redis Pub/Sub 시스템을 통해 발송합니다. 메시지의 형식은 다음과 같습니다:

   [novelId]:[episodeId]:[novelTitle]:[episodeTitle]:[thumbnailFileName]

   - novelId: 소설의 고유 ID. 이 값은 사용자 검색 시 사용될 수 있습니다.
   - episodeId: 에피소드의 고유 ID.
   - novelTitle: 소설의 제목.
   - episodeTitle: 에피소드의 제목.
   - thumbnailFileName: 썸네일 이미지 파일 이름.

   발송된 메시지는 Redis의 "novel-update-channel"이라는 채널을 통해 구독자에게 전달됩니다. 구독자는 이 채널을 통해 소설 업데이트 정보를 실시간으로 수신할 수 있습니다.
   */
    private void publishUpdateMessage(Long novelId, Long episodeId, String novelTitle, String episodeTitle, String thumbnailFileName) {
        //메시지내용 : 앞에는 novelId로 유저 검색시 사용됨
        String message = novelId + ":"
                + episodeId + ":"
                + novelTitle + ":"
                + episodeTitle + ":"
                + thumbnailFileName;
        //Redis에 메시지 발송
        redisMessagePublisher.publish("novel-update-channel", message);
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
    public EpisodeListInfoDto getNovelEpisodesInfo(Long novelId) {
        List<Episode> episodeList = episodeRepository.findByNovel(novelId);
        LocalDateTime lastUpdatedAt = episodeList.isEmpty() ? null : episodeList.get(0).getCreatedAt();
        return EpisodeListInfoDto.builder()
                .chapterCount(episodeList.size())
                .lastUpdatedAt(lastUpdatedAt)
                .build();
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
     * Episode Entity => EpisodeListItemDto 변환 메서드
     *
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
