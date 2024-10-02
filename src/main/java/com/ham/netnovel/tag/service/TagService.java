package com.ham.netnovel.tag.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.dto.TagDataDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;
import com.ham.netnovel.tag.dto.TagFindDto;

import java.util.List;
import java.util.Optional;

public interface TagService {

    /**
     * pk 값으로 Tag 엔티티를 불러오는 메서드 (Null 체크 필수)
     *
     * @param tagId Tag PK id value
     * @return Optional
     */
    Optional<Tag> getTag(Long tagId);

    /**
     * pk 값으로 Tag 엔티티를 불러오는 메서드 (Null 체크 필수)
     *
     * @param tagName Tag PK id value
     * @return Optional
     */
    Optional<Tag> getTagByName(String tagName);


    /**
     * 주어진 태그 이름으로 태그를 검색하여 TagDataDto로 변환하여 반환합니다.
     * 검색 결과가 없을 경우 빈 TagDataDto 객체를 반환합니다.
     *
     * @param tagName 검색할 태그 이름
     * @return 태그 데이터를 담은 {@link  TagDataDto} 객체, 검색 결과가 없을 경우 빈 객체 반환
     */
    TagDataDto getTagDtoByName(String tagName);


    /**
     * 주어진 태그 ID로 태그를 검색하여 TagDataDto로 변환하여 반환합니다.
     * 검색 결과가 없을 경우 빈 TagDataDto 객체를 반환합니다.
     *
     * @param tagId 검색할 태그 ID
     * @return 태그 데이터를 담은 {@link  TagDataDto}객체, 검색 결과가 없을 경우 빈 객체 반환
     */
    TagDataDto getTagDtoById(Long tagId);

    /**
     * 주어진 태그 정보에 따라 태그 데이터 전송 객체(TagDataDto)를 반환합니다.
     *
     * <p>이 메서드는 태그 이름이 "defaultName"일 경우 태그 ID를 사용하여
     * 태그 정보를 검색하고, 그렇지 않으면 태그 이름을 사용하여 검색합니다.</p>
     *
     * @param tagFindDto 태그를 찾기 위한 정보가 포함된 {@link TagFindDto} 객체
     * @return 태그 데이터를 담은 {@link  TagDataDto}객체, 검색 결과가 없을 경우 빈 객체 반환
     */
    TagDataDto getTagDto(TagFindDto tagFindDto);


    /**
     * 주어진 검색어를 기반으로 태그 이름 목록을 반환하는 메서드입니다.
     * <p>
     * 검색어에 포함된 특수 문자를 제거하고, 검색어가 비어있거나 10자를 초과할 경우 빈 리스트를 반환합니다.
     * </p>
     *
     * @param searchWord 검색할 태그의 일부 또는 전체 이름
     * @return 검색어와 일치하는 태그 이름 목록. 검색어가 유효하지 않은 경우 빈 리스트 반환.
     */
    List<String> getTagNamesBySearchWord(String searchWord);


    /**
     * 새로운 태그를 생성하는 메서드입니다.
     *
     * <p>태그명은 검증 후, 이미 존재하는 태그명이 아닐 경우 새롭게 태그를 생성하여 저장합니다.
     * 태그는 한글, 숫자, 영문으로 구성된 10자 이하의 문자열이어야 하며, null 또는 빈 값일 수 없습니다.</p>
     *
     * @param tagName 생성할 태그명 (10자 이하의 한글/숫자/영문으로 구성)
     * @return 생성된 {@link Tag} 엔티티 객체
     * @throws ServiceMethodException 만약 태그명이 이미 존재하거나 예외가 발생할 경우
     */
    Tag createTag(String tagName);

    /**
     * 주어진 태그명을 기준으로 태그를 조회하거나, 존재하지 않으면 새로 생성하는 메서드입니다.
     *
     * <p>태그명이 존재하면 해당 태그를 반환하고, 태그명이 없을 경우 새로운 태그를 생성하여 반환합니다.</p>
     *
     * @param tagName 조회 또는 생성할 태그명 (10자 이하의 한글/숫자/영문으로 구성)
     * @return 기존 또는 새로 생성된 {@link Tag} 엔티티 객체
     * @throws ServiceMethodException 태그 조회 또는 생성 과정에서 발생한 예외
     */
    Tag getOrCreateTag(String tagName);

    /**
     * 등록된 Tag 제거 메서드
     *
     * @param deleteDto Tag
     */
    void deleteTag(TagDeleteDto deleteDto);

    // update 메서드는 존재X
    // name 프로퍼티 밖에 없기 때문에 등록, 삭제 메서드만으로 업데이트 작업하기 충분.
    // 또한 Novel과 Tag가 긴밀하게 연결되어 있기 때문에 일관성 측면에서 업데이트 작업은 위험함.
    // ex : member1과 member2가 각각 novel1, novel2를 생성함. 그리고 두 Novel은 tag1라는 Tag를 공유. Tag는 tag1 태그를 하나만 생성하고 NovelTag에서 novel1 => tag1, novel2 => tag1을 등록함. 그런데 만약 member1이 novel1의 tag1을 tag2로 변경하려고 한다면 (새 Tag tag2을 생성, NovelTag에서 novel1 => tag1 레코드만 삭제, novel1 => tag2 레코드 생성)하면 novel2에 영향을 주지 않음. 하지만 만약 tag1 값을 update 한다면 novel2도 tag1 값이 바뀌는 상황이 발생.
}
