package com.ham.netnovel.novel.repository;

import com.ham.netnovel.novel.data.NovelSortOrder;
import com.ham.netnovel.novel.dto.NovelListDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NovelSearchRepository {

    /**
     * 주어진 정렬 기준과 페이지 정보를 바탕으로 소설 목록을 조회합니다.
     * <p>
     * 이 메서드는 소설의 기본 정보를 조회하고, 각 소설에 대해 태그 정보를 추가로 조회하여 최종 결과를 반환합니다.
     * </p>
     *
     * @param novelSortOrder {@link NovelSortOrder} 소설을 정렬할 기준을 나타내는 열거형 객체
     * @param pageable       {@link Pageable} 페이지 정보를 포함하는 객체 (페이지 번호, 페이지 크기 등)
     * @return {@link List<NovelListDto>} 정렬 기준과 페이지 정보에 따라 조회된 소설 목록을 포함하는 리스트
     */
    List<NovelListDto> findNovelsBySearchConditions(NovelSortOrder novelSortOrder,
                                                    Pageable pageable,
                                                    List<Long> tagIds);


    /**
     * 검색어를 기반으로 소설 목록을 검색하여 반환합니다.
     * <p>
     * 검색된 소설 정보를 NovelListDto로 변환하여 페이징 처리된 결과를 반환합니다.
     * 해당 메서드는 검색어에 대해 Full-Text 검색을 수행하고,
     * 각 소설의 관련 정보를 DTO로 변환하여 반환합니다.
     * </p>
     *
     * @param searchWord 검색어
     * @param pageable       {@link Pageable} 페이지 정보를 포함하는 객체 (페이지 번호, 페이지 크기 등)
     * @return {@link List<NovelListDto>} 정렬 기준과 페이지 정보에 따라 조회된 소설 목록을 포함하는 리스트
     */
    List<NovelListDto> findBySearchWord(String searchWord,Pageable pageable);



    /**
     * 작가명을 통해 소설 목록을 검색하여 반환합니다.
     * <p>이 메서드는 주어진 작가 이름을 포함하는 작가(Member)를 서브쿼리로 검색하여,
     * 해당 작가가 작성한 소설 목록을 반환합니다.</p>
     * <p> 검색 조건에는 작가의 역할이 AUTHOR인지 확인하는 로직이 포함됩니다.</p>
     *
     * @param authorName 작가의 이름을 검색어로 사용
     * @param pageable       {@link Pageable} 페이지 정보를 포함하는 객체 (페이지 번호, 페이지 크기 등)
     * @return {@link List<NovelListDto>} 소설 목록을 포함하는 리스트
     */
    List<NovelListDto> findByAuthorName(String authorName, Pageable pageable);


}
