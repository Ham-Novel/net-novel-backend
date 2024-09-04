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



}
