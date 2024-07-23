package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.Service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.NovelStatus;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Slf4j
public class NovelServiceImpl implements NovelService {
    private final NovelRepository novelRepository;
    private final EpisodeService episodeService;
    private final MemberService memberService;

    @Autowired
    public NovelServiceImpl(NovelRepository novelRepository, EpisodeService episodeService, MemberService memberService) {
        this.novelRepository = novelRepository;
        this.episodeService = episodeService;
        this.memberService = memberService;
    }

    @Override
    @Transactional
    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Novel getNovel(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));
    }

    @Override
    @Transactional
    public Novel createNovel(NovelCreateDto novelCreateDto) {
        log.info("Novel 생성 = {}", novelCreateDto.toString());

        //Author 유저 검증
        Member author = memberService.getMember(novelCreateDto.getAuthorProviderId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Author 입니다."));

        try {
            Novel targetNovel = Novel.builder()
                    .title(novelCreateDto.getTitle())
                    .description(novelCreateDto.getDescription())
                    .author(author)
                    .status(NovelStatus.ONGOING)
                    .build();
            return novelRepository.save(targetNovel);
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovel Error");
        }

    }

    @Override
    public Novel updateNovel(NovelUpdateDto novelUpdateDto) {
        log.info("Novel 변경 = {}", novelUpdateDto.toString());

        //변경할 Novel 검증
        Novel targetNovel = novelRepository.findById(novelUpdateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelUpdateDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 업습니다.");
            }

            //변경사항 novelDetails 내용 일치 검증
            if (novelUpdateDto.isSameContent(targetNovel)) {
                throw new RuntimeException("변경 사항이 없습니다.");
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel Error");
        }

        // Logic
        targetNovel.updateNovel(novelUpdateDto);

        // JPA save() 메소드는 자동으로 변경 감지 => create(X) update(O) 작업 수행
        return novelRepository.save(targetNovel);
    }

    @Override
    public Novel deleteNovel(NovelDeleteDto novelDeleteDto) {
        log.info("Novel 삭제 = {}", novelDeleteDto.toString());

        //삭제할 Novel 검증
        Novel targetNovel = novelRepository.findById(novelDeleteDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelDeleteDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 업습니다.");
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel Error");
        }
        novelRepository.delete(targetNovel);
        return targetNovel;
    }
}
