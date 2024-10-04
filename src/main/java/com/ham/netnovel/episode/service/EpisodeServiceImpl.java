package com.ham.netnovel.episode.service;


import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.service.CoinCostPolicyService;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.message.RedisMessagePublisher;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.repository.EpisodeRepository;
import com.ham.netnovel.episode.data.EpisodeStatus;
import com.ham.netnovel.episode.dto.*;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelMetaData.service.NovelMetaDataService;
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

    private final NovelMetaDataService novelMetaDataService;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, NovelService novelService, CoinCostPolicyService costPolicyService, RedisMessagePublisher redisMessagePublisher, NovelMetaDataService novelMetaDataService) {
        this.episodeRepository = episodeRepository;
        this.novelService = novelService;
        this.costPolicyService = costPolicyService;
        this.redisMessagePublisher = redisMessagePublisher;
        this.novelMetaDataService = novelMetaDataService;
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
                .filter(novel ->
                        //에피소드 생성 요청자와, 소설 작가가 일치하는지 확인
                        episodeCreateDto.getProviderId().equals(novel.getAuthor().getProviderId()))
                //에피소드 작성 요청자와 소설 작가가 다르거나, Novel 정보가 없는경우 예외로 던짐
                .orElseThrow(() ->
                        new IllegalArgumentException("유효하지 않은 Novel 정보이거나 유저 정보가 올바르지 않습니다. novelId= " + episodeCreateDto.getNovelId()));


        CoinCostPolicy costPolicyProperty = costPolicyService.getPolicyEntity(episodeCreateDto.getCostPolicyId())
                //CoinCostPolicy 정보가 없을경우 예외로 던짐
                .orElseThrow(() -> new NoSuchElementException("createEpisode 에러, CoinCostPolicy 정보가 없습니다. coinPolicyId= " + episodeCreateDto.getNovelId()));

        try {
            //새로운 에피소드 엔티티 생성
            Episode targetRecord = Episode.builder()
                    .title(episodeCreateDto.getTitle())
                    .content(episodeCreateDto.getContent())
                    .costPolicy(costPolicyProperty)
                    .novel(novelProperty)
                    .chapter(novelProperty.getEpisodes().size() + 1) //자동으로 넘버링 증가
                    .build();
            //DB에 엔티티 저장
            Episode save = episodeRepository.save(targetRecord);

//            Redis로 Novel이 업데이트 되었다는 메시지를 송부
            publishUpdateMessage(
                    novelProperty.getId(),
                    save.getId(),
                    novelProperty.getTitle(),
                    episodeCreateDto.getTitle(),
                    novelProperty.getThumbnailFileName());

            //NovelMetaData 최근 게시 날짜 업데이트
            novelMetaDataService.updateNovelLatestEpisodeAt(novelProperty.getId(), save.getCreatedAt());


        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("createEpisode 메서드 에러 발생" + ex + ex.getMessage());
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

        //DB에서 에피소드 엔티티 조회, 작가정보와 요청자 정보가 일치하지 않거나 Null 이면 예외로 던짐
        Episode episode = validateEpisodeAuthor(episodeUpdateDto.getEpisodeId(), episodeUpdateDto.getProviderId());

        try {
            //episodeUpdateDto에서 null값은 기존값 할당. 변경할 값만 입력하면 됨.
            String updateTitle = (episodeUpdateDto.getTitle() != null)
                    ? episodeUpdateDto.getTitle() : episode.getTitle();
            String updateContent = (episodeUpdateDto.getContent() != null)
                    ? episodeUpdateDto.getContent() : episode.getContent();
            CoinCostPolicy updateCostPolicy = (episodeUpdateDto.getCostPolicyId() != null)
                    ? costPolicyService.getPolicyEntity(episodeUpdateDto.getCostPolicyId())
                    .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy 정보 없음"))
                    : episode.getCostPolicy();

            //엔티티 정보 업데이트
            episode.updateEpisode(updateTitle, updateContent, updateCostPolicy);
            //DB에 에피소드 엔티티 정보 갱신
            episodeRepository.save(episode);
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("updateEpisode 메서드 에러 발생" + ex + ex.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteEpisode(EpisodeDeleteDto episodeDeleteDto) {
        try {
            //DB에서 에피소드 엔티티 조회, 작가정보와 요청자 정보가 일치하지 않거나 Null 이면 예외로 던짐
            Episode episode = validateEpisodeAuthor(episodeDeleteDto.getEpisodeId(), episodeDeleteDto.getProviderId());

            //status 필드값 변경
            episode.changeStatus(EpisodeStatus.DELETED_BY_USER);
            //엔티티 저장
            episodeRepository.save(episode);
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("deleteEpisode 메서드 에러 발생" + ex + ex.getMessage());
        }
    }

    /**
     * 에피소드의 요청자와 소설 작가가 일치하는지 검증합니다.
     *
     * <p>주어진 에피소드 ID와 제공된 요청자 ID를 사용하여 데이터베이스에서 에피소드를 조회합니다.
     * 조회된 에피소드의 소속 소설의 작가 ID와 요청자의 ID가 일치하는지 확인합니다. 일치하지 않거나
     * 에피소드 정보가 존재하지 않는 경우, {@code NoSuchElementException} 예외가 발생합니다.
     * </p>
     *
     * @param episodeId  에피소드의 ID
     * @param providerId 요청자의 ID
     * @return 유효한 {@link  Episode} 엔티티 객체
     * @throws NoSuchElementException 에피소드 정보가 존재하지 않거나 요청자와 소설 작가가 일치하지 않는 경우
     */
    private Episode validateEpisodeAuthor(Long episodeId, String providerId) {
        return episodeRepository.findById(episodeId).filter(episode ->
                        //요청자와  소설 작가가 일치하는지 확인
                        episode.getNovel().getAuthor().getProviderId().equals(providerId))
                //에피소드가 null 이거나, 요청자와 소설 작가가 일치하지 않으면 예외로 던짐
                .orElseThrow(() -> new NoSuchElementException("validateEpisodeAuthor 메서드 에러, Episode 엔티티 정보 오류"));
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
    public List<EpisodeListItemDto> getEpisodesByConditions(String sortBy, Long novelId, Pageable pageable) {
        try {
            return episodeRepository.findEpisodesByConditions(sortBy, novelId, pageable)
                    .stream()
                    .map(this::convertEntityToListDto) //Episode => EpisodeListItemDto 변환
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            //나머지 Repository 작업 예외 처리
            throw new ServiceMethodException("getNovelByFilter 메서드 에러 발생" + ex + ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEpisodeFree(Long episodeId) {



        //에피소드의 coinCost 객체로 생성
        Integer coinCost = episodeRepository.findById(episodeId)
                .map(episode -> episode.getCostPolicy().getCoinCost())
                .orElseThrow(() ->
                        new IllegalArgumentException("getEpisodeCoinCost 에러, Episode 정보가 없습니다. EpisodeId = " + episodeId));

        //coin cost가 음수면 예외로 던짐
        TypeValidationUtil.validateCoinAmount(coinCost);

        return coinCost == 0;//무료면 true 유료면 false 반환

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
