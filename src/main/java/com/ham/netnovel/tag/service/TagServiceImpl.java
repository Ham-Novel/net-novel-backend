package com.ham.netnovel.tag.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.TagRepository;
import com.ham.netnovel.tag.TagStatus;
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.dto.TagDataDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService{

    TagRepository tagRepository;

    @Autowired
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
    public TagDataDto readTagById(Long tagId) {
        return null;
    }

    @Override
    public TagDataDto readTagByName(String tagName) {
        Tag tagProperty = getTagByName(tagName)
                .orElseThrow(() -> new NoSuchElementException("readTagByName() Error : Doesn't exist Tag."));

        try {
            return TagDataDto.builder()
                    .id(tagProperty.getId())
                    .name(tagProperty.getName())
//                    .status(tagProperty.getStatus())
                    .build();
        } catch (Exception ex) {
            throw new ServiceMethodException("readTagByName() Error : " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public Long createTag(TagCreateDto createDto) {
        try {
            if (tagRepository.existsByName(createDto.getName())) {
                throw new RuntimeException("이미 존재하는 값입니다.");
            }

            Tag newRecord = Tag.builder()
                    .name(createDto.getName())
                    .status(TagStatus.ACTIVE)
                    .build();
            Tag saved = tagRepository.save(newRecord);
            return saved.getId();
        } catch (Exception ex) {
            throw new ServiceMethodException("createTag() Error : " + ex.getMessage());
        }
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
}
