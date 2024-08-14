package com.ham.netnovel.common.utils;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

public class PageableUtil {

    /**
     * 페이지네이션을 위한 Pageable 객체를 생성하는 메서드
     * @param pageNumber 페이지 번호 (0부터 시작)
     * @param pageSize   페이지당 항목 수
     * @return Pageable 반환될 객체 타입
     */
    public static Pageable createPageable(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageSize == null) {
            throw new IllegalArgumentException("createPageable 에러: 파라미터가 null 입니다.");
        } else if (pageNumber <0 || pageSize <0) {
            throw new IllegalArgumentException("createPageable 에러: 파라미터가 음수입니다.");
        }

        return PageRequest.of(pageNumber, pageSize);
    }
}
