package com.ham.netnovel.tag.service;

import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;

import java.util.Optional;

public interface TagService {

    /**
     * pk 값으로 Tag 엔티티를 불러오는 메서드 (Null 체크 필수)
     * @param tagId Tag PK id value
     * @return Optional
     */
    Optional<Tag> getTag(Long tagId);

    /**
     * pk 값으로 Tag 엔티티를 불러오는 메서드 (Null 체크 필수)
     * @param tagName Tag PK id value
     * @return Optional
     */
    Optional<Tag> getTagByName(String tagName);


    /**
     * 새 Tag 등록 메서드
     * @param createDto Tag 콘텐츠가 들어 있음.
     * @return Long 생성한 Tag 레코드의 PK 값
     */
    Long createTag(TagCreateDto createDto);

    /**
     * 등록된 Tag 제거 메서드
     * @param deleteDto Tag
     */
    void deleteTag(TagDeleteDto deleteDto);

    // update 메서드는 존재X
    // name 프로퍼티 밖에 없기 때문에 등록, 삭제 메서드만으로 업데이트 작업하기 충분.
    // 또한 Novel과 Tag가 긴밀하게 연결되어 있기 때문에 일관성 측면에서 업데이트 작업은 위험함.
    // ex : member1과 member2가 각각 novel1, novel2를 생성함. 그리고 두 Novel은 tag1라는 Tag를 공유. Tag는 tag1 태그를 하나만 생성하고 NovelTag에서 novel1 => tag1, novel2 => tag1을 등록함. 그런데 만약 member1이 novel1의 tag1을 tag2로 변경하려고 한다면 (새 Tag tag2을 생성, NovelTag에서 novel1 => tag1 레코드만 삭제, novel1 => tag2 레코드 생성)하면 novel2에 영향을 주지 않음. 하지만 만약 tag1 값을 update 한다면 novel2도 tag1 값이 바뀌는 상황이 발생.
}
