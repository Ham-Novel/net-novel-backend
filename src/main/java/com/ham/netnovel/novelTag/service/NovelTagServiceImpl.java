package com.ham.netnovel.novelTag.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelTag.NovelTag;
import com.ham.netnovel.novelTag.NovelTagId;
import com.ham.netnovel.novelTag.NovelTagRepository;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagDeleteDto;
import com.ham.netnovel.novelTag.dto.NovelTagListDto;
import com.ham.netnovel.tag.Tag;
import com.ham.netnovel.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class NovelTagServiceImpl implements NovelTagService {

    private final NovelTagRepository novelTagRepository;
    private final NovelService novelService;
    private final TagService tagService;

    @Autowired
    public NovelTagServiceImpl(NovelTagRepository novelTagRepository, NovelService novelService, TagService tagService) {
        this.novelTagRepository = novelTagRepository;
        this.novelService = novelService;
        this.tagService = tagService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NovelTag> getNovelTag(Long novelId, Long tagId) {
        return novelTagRepository.findById(new NovelTagId(novelId, tagId));
    }

    @Override
    public List<NovelTagListDto> getTagsByNovel(Long novelId) {
        List<NovelTag> novelTags = novelTagRepository.findByIdNovelId(novelId);

        return novelTags.stream()
                .map(NovelTag::getTag)
                .map(this::convertTagToListDto)
                .toList();
    }

    NovelTagListDto convertTagToListDto(Tag tag) {
        return NovelTagListDto.builder()
                .tagId(tag.getId())
                .tagName(tag.getName())
                .build();
    }

    @Override
    @Transactional
    public Boolean createNovelTag(NovelTagCreateDto createDto) {
        //작품 레코드 조회, 검증하여 문제없을경우 객체 생성
        Novel novel = novelService.getNovel(createDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("createNovelTag() Error : Novel return value is null. novelId=" + createDto.getNovelId()));

        //태그 엔티티 객체 생성, DB에 있을경우 가져오고 없을경우 생성해서 가져옴
        Tag tag = tagService.getOrCreateTag(createDto.getTagName());

        try {
            //NovelTag 생성에 사용할 NovelTagId 값 생성
            NovelTagId novelTagId = new NovelTagId(novel.getId(), tag.getId());

            //NovelTag 엔티티가 있을경우 false 반환, 없을경우 새로 생성후 true 반환
            return novelTagRepository.findById(novelTagId)
                    .map(novelTag -> {
                        log.warn("이미 소설에 태그 정보가 등록되어 있습니다. novel id={}, tagId={}",
                                novel.getId(), tag.getId());
                        //false 반환
                        return false;
                    })
                    .orElseGet(() -> {

                        novelTagRepository.save(NovelTag.builder()
                                .id(novelTagId)
                                .novel(novel)
                                .tag(tag)
                                .build());

                        return true;
                    });

        } catch (Exception ex) {
            throw new ServiceMethodException("createNovelTag 메서드 에러 : " + ex + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteNovelTag(NovelTagDeleteDto deleteDto) {

        Tag tagByName = tagService.getTagByName(deleteDto.getTagName())
                .orElseThrow(() -> new NoSuchElementException("deleteNovelTag 메서드 에러, Tag 엔티티가 null 입니다. tagName = " + deleteDto.getTagName()));

        //NovelTag 레코드 조회 검증
        NovelTagId novelTagId = new NovelTagId(deleteDto.getNovelId(), tagByName.getId());
        NovelTag novelTag = novelTagRepository.findById(novelTagId)
                .orElseThrow(() -> new NoSuchElementException("deleteNovelTag() Error : NovelTag return value is null. novelTagId=" + novelTagId));
        try {
            //해당 NovelTag 삭제
            novelTagRepository.delete(novelTag);

        } catch (Exception ex) {
            throw new ServiceMethodException("deleteNovelTag() Error : " + ex.getMessage());
        }

    }
}
