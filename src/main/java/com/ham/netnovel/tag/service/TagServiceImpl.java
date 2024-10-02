package com.ham.netnovel.tag.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.TagRepository;
import com.ham.netnovel.tag.TagStatus;
import com.ham.netnovel.tag.dto.TagDataDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;
import com.ham.netnovel.tag.dto.TagFindDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTag(Long tagId) {
        return tagRepository.findById(tagId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTagByName(String tagName) {
        return tagRepository.findByName(tagName);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDataDto getTagDtoByName(String tagName) {
        //DB에서 검색하여 결과 DTO로 변환하여 반환, 검색결과가 없을경우 빈객체 반환
        return getTagByName(tagName)
                .map(this::convertToTagDataDto) // 값이 있을 경우 DTO 변환
                .orElse(new TagDataDto());
    }

    @Override
    public TagDataDto getTagDtoById(Long tagId) {
        // tagId가 null인 경우 바로 빈 객체 반환
        if (tagId == null) {
            return new TagDataDto();
        }

        //DB에서 검색하여 결과 DTO로 변환하여 반환, 검색결과가 없을경우 빈객체 반환
        return tagRepository.findById(tagId)
                .map(this::convertToTagDataDto)// 값이 있을 경우 DTO 변환
                .orElse(new TagDataDto());

    }

    @Override
    @Transactional(readOnly = true)
    public TagDataDto getTagDto(TagFindDto tagFindDto) {

        try {
            //태그이름이 기본값일경우 ID 로 검색
            if ("defaultName".equals(tagFindDto.getTagName())) {
                return getTagDtoById(tagFindDto.getId());
            }
            //태그이름이 기본값이 아닐경우 태그명으로 검색
            return getTagDtoByName(tagFindDto.getTagName());
        } catch (Exception ex) {

            throw new ServiceMethodException("getTagDto 에러 : " + ex + ex.getMessage());

        }


    }


    @Override
    public List<String> getTagNamesBySearchWord(String searchWord) {

        //파라미터 검증, SQL injection 에 사용되는 특수문자 제거,
        String validateSearchWord = TypeValidationUtil.validateSearchWord(searchWord);

        //검색어가 비어있거나 10자를 넘어가는 경우 빈리스트 반환
        if (validateSearchWord.isEmpty() || validateSearchWord.length() > 10) {
            return Collections.emptyList();
        }

        //검색어 기반으로 태그 검색하여 반환
        return tagRepository.findBySearchWord(validateSearchWord);

    }

    @Override
    @Transactional
    public Tag createTag(String tagName) {

        //파라미터 검증, 태그가 null 이거나 비어있으면 안되고, 10글자 이하의 한글/숫자/영문 이여야함
        String validateTagName = validateTagName(tagName);

        //태그명이 존재하는 경우 예외로 던짐
        if (tagRepository.existsByName(validateTagName)) {
            throw new ServiceMethodException("createTag 에러, 동일한 태그명이 존재합니다. 태그명={}" + tagName);
        }

        try {
            //새로운 tag 엔티티 객체 생성
            Tag tag = Tag.builder()
                    .name(validateTagName)
                    .status(TagStatus.ACTIVE)
                    .build();

            //만들어진 Tag 엔티티 반환

            Tag save = tagRepository.save(tag);
            log.info("새로운 Tag 생성 완료, 태그명 ={} ", save.getId());
            return save;

        } catch (Exception ex) {
            throw new ServiceMethodException("createTag() Error : " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public Tag getOrCreateTag(String tagName) {
        try {
            //태그 엔티티를 가져와서 반환하거나,
            // DB에 태그 엔티티가 없으면 새로만들어서 반환
            Optional<Tag> tagByName = getTagByName(tagName);

            return tagByName.orElseGet(() -> createTag(tagName));
        } catch (Exception ex) {
            throw new ServiceMethodException("getOrCreateTag 에러 : " + ex + ex.getMessage());
        }

    }

    /**
     * 태그명의 유효성을 검사하는 메서드 입니다.
     *
     * <p>
     * 태그명이 null 이거나 객체가 비어있는 경우 예외로 던집니다.
     * </p>
     *
     * <p>
     * 태그명 앞뒤 공백을 제거한후 10자 이상이거나, 영어/한글/숫자 외의 문자가
     * 포함된경우 예외로 던집니다.
     * </p>
     *
     * @param tagName 태그명
     * @return 앞뒤 공백을 제거한 태그명 {@link String} 객체
     */
    private String validateTagName(String tagName) {

        //파라미터 null, 비어있는지 체크
        if (tagName == null || tagName.isEmpty()) {
            throw new IllegalArgumentException("validateTagName 메서드 에러, 파라미터가 null 이거나 비었습니다.");
        }

        //비어있지 않으면, 앞뒤 공백 제거한 객체 생성
        String trimmedName = tagName.trim();

        //앞뒤 공백 제거한 길이가 10자리 이상이면 예외로 던짐
        if (trimmedName.length() > 10) {
            throw new IllegalArgumentException("validateTagName 메서드 에러, 파라미터가 10자를 넘어갑니다. tagName= " + tagName);
        }

        // 영어, 숫자, 한글, 중간 공백만 허용하는 정규식,
        if (!trimmedName.matches("[a-zA-Z0-9가-힣 ]+")) {
            throw new IllegalArgumentException("validateTagName 메서드 에러, 영어, 숫자, 한글 및 중간 공백만 허용됩니다. tagName= " + tagName);
        }

        //이상 없으면 앞뒤공백 자른 태그이름 반환
        return trimmedName;


    }

    @Override
    @Transactional
    public void deleteTag(TagDeleteDto deleteDto) {
        Tag targetRecord = tagRepository.findById(deleteDto.getTagId())
                .orElseThrow(() -> new NoSuchElementException("Tag 정보 없음"));
        try {
            targetRecord.changeStatus(TagStatus.DELETED_BY_USER);
            tagRepository.save(targetRecord);
        } catch (Exception ex) {
            throw new ServiceMethodException("deleteTag() Error : " + ex.getMessage());
        }
    }


    //null 파라미터 넣지 말것!!
    private TagDataDto convertToTagDataDto(Tag tag) {

        if (tag == null) {
            return new TagDataDto();
        }
        return TagDataDto.builder()
                .name(tag.getName())
                .id(tag.getId())
                .status(tag.getStatus())
                .build();

    }


}
