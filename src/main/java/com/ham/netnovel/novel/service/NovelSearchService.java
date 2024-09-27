package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.data.NovelSearchType;
import com.ham.netnovel.novel.dto.NovelListDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NovelSearchService {

    /**
     * 주어진 정렬 기준과 페이지 정보를 바탕으로 소설 목록을 조회하는 메서드입니다.
     *
     * <p>
     * 이 메서드는 기본적으로 소설 목록을 조회수 기준으로 정렬합니다.
     * </p>
     *
     * <p>
     * 사용자가 제공한 정렬 기준에 따라, 소설 목록을 좋아요 순 또는 최신순으로 정렬할 수 있습니다.
     * 조회된 소설 목록의 썸네일 URL은 S3 서비스의 CloudFront URL로 변환됩니다.
     * </p>
     * @param sortOrder 소설 목록의 정렬 기준을 나타내는 문자열입니다.
     *                  "favorites"는 좋아요 순, "latest"는 최신순, 기본값은 "view"로 조회수 순입니다.
     * @param pageable 페이지 정보 및 페이징 조건을 포함하는 {@link Pageable} 객체입니다.
     *
     * @return List<NovelListDto> 소설 목록을 포함한 DTO 리스트를 반환합니다.
     *         각 소설의 썸네일 URL은 CloudFront URL로 변환되어 반환됩니다.
     * @throws ServiceMethodException 검색 중 예외 발생 시 예외를 던집니다.
     */
    List<NovelListDto> getNovelsBySearchCondition(String sortOrder, Pageable pageable, List<Long> tagIds);


    /**
     * 검색어를 기반으로 소설 목록을 검색하여 반환합니다.
     * <p>
     * 검색된 소설 정보를 NovelListDto로 변환하여 페이징 처리된 결과를 반환합니다.
     * 조회된 소설 목록의 썸네일 URL은 S3 서비스의 CloudFront URL로 변환됩니다.
     * </p>
     *
     * @param searchWord 유저가 입력한  검색어 {@link String} 객체
     * @param pageable 페이지 정보 및 페이징 조건을 포함하는 {@link Pageable} 객체입니다.
     * @return List<NovelListDto> 소설 목록을 포함한 DTO 리스트를 반환합니다.
     *         각 소설의 썸네일 URL은 CloudFront URL로 변환되어 반환됩니다.     *

     * @throws ServiceMethodException 검색 중 예외 발생 시 예외를 던집니다.
     */
    List<NovelListDto> getNovelsBySearchWord(String searchWord,
                                             NovelSearchType novelSearchType,
                                             Pageable pageable);




    /**
     * 주어진 기간에 따라 소설의 랭킹을 조회하여 해당 페이지의 소설 정보를 반환합니다.
     *
     * <p>이 메서드는 다음과 같은 작업을 수행합니다:</p>
     * <ul>
     *     <li>페이지 번호와 페이지 크기를 사용하여 데이터의 시작 인덱스와 끝 인덱스를 계산합니다.</li>
     *     <li>Redis에서 주어진 기간에 해당하는 소설 랭킹 데이터를 가져옵니다.</li>
     *     <li>가져온 데이터에서 소설 ID를 추출합니다.</li>
     *     <li>추출한 소설 ID를 사용하여 소설 엔티티를 조회하고, 랭킹 순서로 정렬하여 DTO로 변환합니다.</li>
     * </ul>
     *
     * <p>작업 중 예외가 발생하면 {@link ServiceMethodException}이 발생합니다.</p>
     *
     * @param period 소설 랭킹을 조회할 기간. "daily", "weekly", "monthly" 중 하나로 지정합니다.
     * @param pageable 페이지 정보. 페이지 번호와 페이지 크기를 포함합니다.
     * @return 주어진 페이지와 기간에 해당하는 소설 정보를 담은 {@link List}입니다.
     * @throws ServiceMethodException 메서드 실행 중 오류가 발생한 경우 발생합니다.
     */
    List<NovelListDto> getNovelsByRanking(String period, Pageable pageable);




    /**
     * 유저의 선호작 Novel 리스트 반환.
     * @param providerId 유저 PK 값.
     * @return List<Novel>
     */
    List<Novel> getFavoriteNovels(String providerId);

}
