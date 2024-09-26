package com.ham.netnovel.novelTag.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novelTag.NovelTag;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagDeleteDto;
import com.ham.netnovel.novelTag.dto.NovelTagListDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface NovelTagService {

    /**
     * novel id와 tag id로 NovelTag 엔티티 가져오는 메서드
     * @param tagId Tag PK 값
     * @param novelId Novel PK 값
     * @return Optional
     */
    Optional<NovelTag> getNovelTag(Long novelId, Long tagId);


    /**
     * 특정 작품의 모든 태그를 반환
     * @param novelId
     * @return List
     */
    List<NovelTagListDto> getTagsByNovel(Long novelId);

    /**
     * 소설과 태그를 연결하는 메서드입니다.
     *
     * <p>주어진 소설 ID와 태그명을 기반으로 태그를 소설에 연결합니다.
     * 태그가 존재하지 않을 경우 레코드를 생성한 후 소설에 태그를 추가합니다.</p>
     *
     * @param createDto 소설 ID와 태그명을 포함하는 {@link NovelTagCreateDto} 객체
     * @return 소설에 태그를 성공적으로 추가하면 {@code true}, 이미 태그가 존재하면 {@code false} 반환
     * @throws NoSuchElementException 소설 ID로 소설을 찾을 수 없을 때 발생
     * @throws ServiceMethodException 태그 추가 과정에서 예외 발생 시 발생
     */
    Boolean createNovelTag(NovelTagCreateDto createDto);


    void deleteNovelTag(NovelTagDeleteDto deleteDto);
}
