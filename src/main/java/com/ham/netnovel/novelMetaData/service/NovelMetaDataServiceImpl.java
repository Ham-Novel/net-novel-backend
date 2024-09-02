package com.ham.netnovel.novelMetaData.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelMetaData.NovelMetaData;
import com.ham.netnovel.novelMetaData.data.MetaDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NovelMetaDataServiceImpl implements NovelMetaDataService {

    private final NovelService novelService;


    private final NovelMetaDataRepository novelMetaDataRepository;


    public NovelMetaDataServiceImpl(NovelService novelService, NovelMetaDataRepository novelMetaDataRepository) {
        this.novelService = novelService;
        this.novelMetaDataRepository = novelMetaDataRepository;
    }


    @Override
    public void updateNovelTotalViews(Integer pageSize, Integer pageNumber) {
        processMetaData(1000,0, novelService::getNovelWithTotalViews,MetaDataType.VIEW);
    }

    @Override
    public void updateNovelTotalFavorites(Integer pageSize, Integer pageNumber) {
        processMetaData(1000,0, novelService::getNovelWithTotalFavorites, MetaDataType.FAVORITE);

    }
    @Override
    public void updateNovelLatestEpisodeAt(Integer pageSize, Integer pageNumber) {
        processMetaData(1000,0, novelService::getNovelWithLatestEpisodeCreateTime, MetaDataType.DATE);
    }

    /**
     * 페이지 단위로 메타데이터를 가져와 처리하는 메서드 입니다.
     *
     * <p>
     * 메타데이터는 지정된 페이지 메타데이터 제공 함수로부터 페이지 단위로 가져오며,
     * 각 페이지에 대해 메타데이터를 업데이트하거나 새로 생성합니다.
     * </p>
     *
     * @param pageSize 페이지 당 아이템 수 {@link Integer}객체
     * @param pageNumber 현재 페이지 번호 {@link Integer}객체
     * @param pageMetaDataProvider 메타데이터를 공급하는 함수{@link Function} 객체. 페이지 정보에 따라 메타데이터를 반환
     * @param metaDataType 업데이트할 {@link MetaDataType} enum 객체 (예: 조회수, 즐겨찾기, 최신 에피소드 날짜)
     */
    private <V> void processMetaData(Integer pageSize,
                                     Integer pageNumber,
                                     Function<Pageable, Map<Long, V>> pageMetaDataProvider,
                                     MetaDataType metaDataType) {

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            // 메타데이터를 페이지에 따라 가져옴
            Map<Long, V> novelWithMetaData = pageMetaDataProvider.apply(pageable);

            if (novelWithMetaData == null || novelWithMetaData.isEmpty()) {
                // 메타데이터가 없으면 종료(모든 novel에 대한 메타 데이터 갱신 완료)
                log.info(" {} 갱신 완료, 메서드 종료", metaDataType);
                break;
            }
            log.info("{} 메타 데이터 업데이트 {} 번째 실행, 페이지 사이즈: {}", metaDataType, pageNumber, pageSize);
            //novelId 만 추출하여 Set 객체에 할당
            Set<Long> novelIds = novelWithMetaData.keySet();
            //novelId 로 메타데이터 엔티티를 불러옴, 엔티티 업데이트시 사용
            Map<Long, NovelMetaData> existingNovelMetaData = getExistingNovelMetaData(novelIds);
            // 메타데이터를 업데이트 또는 생성
            updateOrCreateNovelMetaData(novelIds, existingNovelMetaData, novelWithMetaData,metaDataType);
            // 다음 페이지 반복문 실행
            pageNumber++;
        }

    }

    /**
     * 주어진 소설 ID 목록에 대해 메타데이터를 처리하고, 해당 메타데이터를 생성하거나 업데이트합니다.
     * 'type' 파라미터에 따라 총 좋아요 수 또는 총 조회수를 업데이트합니다.
     * 만약 소설에 대한 메타데이터가 존재하지 않으면 새롭게 생성하여 저장합니다.
     *
     * @param novelIds              메타데이터를 처리할 소설 ID들의 집합
     * @param existingNovelMetaData 이미 존재하는 소설 메타데이터의 {@link Map} 객체 (소설 ID를 키로 사용)
     * @param novelWithMetaData     소설 ID와 관련된 즐겨찾기 수 또는 조회수의 {@link Map} 객체
     * @param type                  처리할 메타데이터의 타입 ("favorites" 또는 "views")
     * @throws ServiceMethodException 유효하지 않은 'type'이 전달될 경우 발생
     */
    private <V> void updateOrCreateNovelMetaData(Set<Long> novelIds,
                                                 Map<Long, NovelMetaData> existingNovelMetaData,
                                                 Map<Long, V> novelWithMetaData,
                                                 MetaDataType type) {

        List<NovelMetaData> novelMetaDataList = new ArrayList<>();

        for (Long novelId : novelIds) {

            //각 파라미터 변수 선언 및 기본값 할당
            int totalFavorites = 0;
            Long totalViews = 0L;
            LocalDateTime latestDate = LocalDateTime.of(2000, 1, 1, 0, 0);

            //DB에서 Novel의 기존 메타 데이터 엔티티 불러옴
            NovelMetaData novelMetaData = existingNovelMetaData.get(novelId);

            //입력 파라미터 타입에 따른 변수값 수정
            switch (type) {
                case FAVORITE -> totalFavorites = (int) novelWithMetaData.get(novelId);
                case VIEW -> totalViews = (Long) novelWithMetaData.get(novelId);
                case DATE -> latestDate = (LocalDateTime) novelWithMetaData.get(novelId);
                default -> throw new ServiceMethodException("updateOrCreateNovelMetaData에러, type이 유효하지 않습니다.");
            }

            // 메타데이터가 존재하지 않을 경우 새로 생성
            if (novelMetaData == null) {
                Optional<Novel> novel = novelService.getNovel(novelId);//Novel 엔티티 DB에서 조회
                // 소설이 없을 경우 경고 로그를 남기고 계속 진행
                if (novel.isEmpty()) {
                    log.warn("Novel 엔티티 조회 에러 , novelId ={}", novelId);
                    continue;
                }
                // 새로운 NovelMetaData 엔티티 생성
                NovelMetaData createdNovelMetaData = NovelMetaData.builder()
                        .totalFavorites(totalFavorites)
                        .novel(novel.get())
                        .totalViews(totalViews)
                        .latestEpisodeAt(latestDate)
                        .build();

                //List에 엔티티 저장
                novelMetaDataList.add(createdNovelMetaData);

                //로그출력
                log.info("NovelMetaData 엔티티 생성, novelId={}", novelId);
            } else {
                switch (type) {
                    case FAVORITE -> novelMetaData.updateTotalFavorites(totalFavorites);
                    case VIEW -> novelMetaData.updateTotalViews(totalViews);
                    case DATE -> novelMetaData.updatedLatestEpisodeAt(latestDate);
                    default -> throw new ServiceMethodException("updateOrCreateNovelMetaData에러, type이 유효하지 않습니다.");
                }
                //List에 엔티티 저장
                novelMetaDataList.add(novelMetaData);
                log.info("NovelMetaData 엔티티 업데이트, novelId={}", novelId);
            }
        }
        //DB에 엔티티 List 정보 업데이트
        try {
            novelMetaDataRepository.saveAll(novelMetaDataList);
            log.info("NovelMetaData 엔티티 DB 저장 완료, 엔티티 수 ={}", novelMetaDataList.size());

        } catch (Exception ex) {
            throw new ServiceMethodException("updateOrCreateNovelMetaData 메서드 에러" + ex + ex.getMessage());
        }
        //DB에 엔티티 모두 저장
    }

    /**
     * 주어진 소설 ID 목록에 대해 기존의 소설 메타데이터를 조회합니다.
     *
     * <p>
     * 이 메소드는 주어진 소설 ID들의 집합을 사용하여 데이터베이스에서 해당 소설들의 메타데이터를 조회하고,
     * 조회된 결과를 소설 ID를 키로 하고, 메타데이터 객체를 값으로 하는 맵(Map) 형태로 반환합니다.
     * </p>
     *
     * @param novelIds 메타데이터를 조회할 소설 ID들의 집합
     * @return 소설 ID({@link Long})를 키, 해당 소설의 {@link NovelMetaData} 객체를 값으로 하는 {@link Map} 객체
     */

    private Map<Long, NovelMetaData> getExistingNovelMetaData(Set<Long> novelIds) {
        return novelMetaDataRepository.findByNovelId(novelIds)
                .stream()
                .collect(Collectors.toMap(
                        object -> object.getNovel().getId(),
                        object -> object));
    }
}

