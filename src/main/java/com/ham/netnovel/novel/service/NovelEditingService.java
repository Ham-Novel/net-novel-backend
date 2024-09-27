package com.ham.netnovel.novel.service;


import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

//소설 생성/업데이트 관련 로직을 담는 서비스계층
public interface NovelEditingService {

    /**
     * 주어진 소설 생성 DTO를 기반으로 새로운 소설을 생성하는 메서드입니다.
     *
     * <p>유저 정보를 DB에서 찾고, 없으면 예외로 던집니다.</p>
     * <p>DTO에 등록된 정보로 소설 엔티티를 생성하여 DB에 저장합니다.</p>
     * <p>유저의 ROLE이 READER인 경우 AUTHOR로 변경하여 DB에 저장합니다.</p>
     *
     * @param novelCreateDto 새 소설의 데이터가 포함된 {@link NovelCreateDto} 객체입니다.
     * @return 생성된 소설의 ID입니다.
     * @throws NoSuchElementException 해당 작가가 존재하지 않을 경우 발생합니다.
     * @throws ServiceMethodException 소설 생성 중 오류가 발생할 경우 발생합니다.
     */
    Long createNovel(NovelCreateDto novelCreateDto);



    Boolean updateNovel(NovelUpdateDto novelUpdateDto) throws AccessDeniedException;




}
