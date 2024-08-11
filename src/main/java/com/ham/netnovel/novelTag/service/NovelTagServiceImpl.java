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
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.dto.TagDeleteDto;
import com.ham.netnovel.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    public NovelTagId createNovelTag(NovelTagCreateDto createDto) {
        //작품 레코드 조회 검증
        Novel novel = novelService.getNovel(createDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("createNovelTag() Error : Novel return value is null. novelId=" + createDto.getNovelId()));
        try {
            //같은 이름의 태그 레코드 존재하는지 조회
            Tag tag = tagService.getTagByName(createDto.getTagName())
                    .orElseGet(() -> {
                        //존재하지 않는다면 새로 생성.
                        Long newTagId = tagService.createTag(TagCreateDto.builder().name(createDto.getTagName()).build());
                        return tagService.getTag(newTagId)
                                .orElseThrow(() -> new NoSuchElementException("Error In Create New Tag"));
                    });

            //NovelTag 생성에 사용할 NovelTagId 값 생성
            NovelTagId novelTagId = new NovelTagId(novel.getId(), tag.getId());

            //NovelTag 레코드가 존재하는지 조회 검증
            novelTagRepository.findById(novelTagId)
                    .ifPresent((value) -> {
                        throw new DataIntegrityViolationException("Already Existing NovelTag Record");
                    });

            //DB에 저장
            novelTagRepository.save(NovelTag.builder()
                            .id(novelTagId)
                            .novel(novel)
                            .tag(tag)
                    .build());

            return novelTagId;

        } catch (Exception ex) {
            throw new ServiceMethodException("createNovelTag() Error : "  + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteNovelTag(NovelTagDeleteDto deleteDto) {
        //작품 레코드 조회 검증
        Novel novel = novelService.getNovel(deleteDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("deleteNovelTag() Error : Novel return value is null. novelId=" + deleteDto.getNovelId()));

        //태그 레코드 조회 검증
        Tag tag = tagService.getTag(deleteDto.getTagId())
                .orElseThrow(() -> new NoSuchElementException("deleteNovelTag() Error : Tag return value is null. tagId=" + deleteDto.getNovelId()));

        //NovelTag 레코드 조회 검증
        NovelTagId novelTagId = new NovelTagId(novel.getId(), tag.getId());
        NovelTag novelTag = novelTagRepository.findById(novelTagId)
                .orElseThrow(() -> new NoSuchElementException("deleteNovelTag() Error : NovelTag return value is null. novelTagId=" + novelTagId));

        try {
            //해당 NovelTag 삭제
            novelTagRepository.delete(novelTag);

            //해당 Novel에서 삭제하려는 Tag가 어떤 Novel하고도 연결되있지 않다면 Tag 삭제.
            if (tag.getNovelTags().isEmpty()) {
                tagService.deleteTag(TagDeleteDto.builder().tagId(tag.getId()).build());
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("deleteNovelTag() Error : "  + ex.getMessage());
        }

    }
}
