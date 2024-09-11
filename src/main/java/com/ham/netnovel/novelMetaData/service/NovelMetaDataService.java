package com.ham.netnovel.novelMetaData.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novelMetaData.NovelMetaData;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface NovelMetaDataService {



    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 총 조회수를 업데이트합니다.
     *
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelTotalViewsByPage(Integer pageSize, Integer pageNumber);


    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 총 좋아요 수를 업데이트합니다.
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelTotalFavoritesByPage(Integer pageSize, Integer pageNumber);

    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 최신 에피소드 생성 시간을 업데이트합니다.
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelLatestEpisodeAtByPage(Integer pageSize, Integer pageNumber);



    /**
     * 지정된 소설 ID에 대한 최신 에피소드 날짜를 업데이트합니다.
     *
     * <p>소설 ID로 {@link NovelMetaData}를 조회한 후, 해당 엔티티의 최신 에피소드 날짜를
     * 주어진 날짜로 업데이트합니다.</p>
     *
     * <p>만약 해당 소설의 메타데이터가 존재하지 않는 경우,
     * 새로운 {@link NovelMetaData} 엔티티를 생성하여 저장합니다.</p>
     *
     * @param novelId 소설의 ID
     * @param latestDate 소설의 최신 에피소드 {@link LocalDateTime} 객체
     * @throws ServiceMethodException 예외 발생시
     */
    void updateNovelLatestEpisodeAt(Long novelId, LocalDateTime latestDate);




    Map<Long, NovelMetaData> getExistingNovelMetaData(Set<Long> novelIds);





}
