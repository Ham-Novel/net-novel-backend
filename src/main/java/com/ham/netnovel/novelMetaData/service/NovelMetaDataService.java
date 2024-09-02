package com.ham.netnovel.novelMetaData.service;

public interface NovelMetaDataService {



    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 총 조회수를 업데이트합니다.
     *
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelTotalViews(Integer pageSize, Integer pageNumber);


    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 총 좋아요 수를 업데이트합니다.
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelTotalFavorites(Integer pageSize, Integer pageNumber);

    /**
     * 주어진 페이지 크기와 페이지 번호를 사용하여 소설의 최신 에피소드 생성 시간을 업데이트합니다.
     *
     * @param pageSize 페이지 당 아이템 수
     * @param pageNumber 현재 페이지 번호
     */
    void updateNovelLatestEpisodeAt(Integer pageSize, Integer pageNumber);

}
