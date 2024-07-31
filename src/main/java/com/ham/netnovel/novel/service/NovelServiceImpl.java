package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelResponseDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Slf4j
public class NovelServiceImpl implements NovelService {
    private final NovelRepository novelRepository;
    private final MemberService memberService;

    @Autowired
    public NovelServiceImpl(NovelRepository novelRepository, MemberService memberService) {
        this.novelRepository = novelRepository;
        this.memberService = memberService;
    }

    //TODO 각 Method마다 주석 작성
    @Override
    @Transactional
    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public NovelResponseDto getNovel(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."))
                .parseResponseDto();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Novel> getNovelEntity(Long id) {
        return novelRepository.findById(id);
    }

    @Override
    @Transactional
    public NovelResponseDto createNovel(NovelCreateDto novelCreateDto) {
        log.info("Novel 생성 = {}", novelCreateDto.toString());

        //Member Entity 조회 -> Author 검증
        Member author = memberService.getMember(novelCreateDto.getAccessorProviderId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Member 입니다."));

        try {
            //Novel 생성
            Novel targetNovel = Novel.builder()
                    .title(novelCreateDto.getTitle())
                    .description(novelCreateDto.getDescription())
                    .author(author)
                    .type(NovelType.ONGOING)
                    .status(NovelStatus.ACTIVE)
                    .build();
            return novelRepository.save(targetNovel).parseResponseDto();
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovel Error" + ex.getMessage());
        }

    }

    @Override
    @Transactional
    public NovelResponseDto updateNovel(NovelUpdateDto novelUpdateDto) {
        log.info("Novel 변경 = {}", novelUpdateDto.toString());

        //변경할 Novel 검증
        Novel targetNovel = novelRepository.findById(novelUpdateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelUpdateDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 없습니다.");
            }

            //변경사항 novelDetails 내용 일치 검증
            if (isSameContent(targetNovel,
                    novelUpdateDto.getTitle(),
                    novelUpdateDto.getDescription(),
                    novelUpdateDto.getType())) {
                throw new RuntimeException("변경 사항이 없습니다.");
            }

        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel Error" + ex.getMessage());
        }

        // Logic
        targetNovel.updateNovel(novelUpdateDto);

        // JPA save() 메소드는 자동으로 변경 감지 => create(X) update(O) 작업 수행
        return novelRepository.save(targetNovel).parseResponseDto();
    }

    @Override
    @Transactional
    public NovelResponseDto deleteNovel(NovelDeleteDto novelDeleteDto) {
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
            throw new ServiceMethodException("updateNovel Error" + ex.getMessage());
        }

        //Novel 삭제 처리
        targetNovel.changeStatus(NovelStatus.DELETED_BY_USER);
        novelRepository.delete(targetNovel);
        return targetNovel.parseResponseDto();
    }

    public boolean isSameContent(Novel novel, String title, String desc, NovelType status) {
        return novel.getTitle().equals(title)
                && novel.getDescription().equals(desc)
                && novel.getType().equals(status);
    }


    //단순히 엔티티 List만 반환하는 메서드
    //Null체크, DTO 변환은 MemberMyPageService에서 진행
    @Override
    @Transactional(readOnly = true)
    public List<Novel> getFavoriteNovels(String providerId) {

        try {
            return novelRepository.findFavoriteNovelsByMember(providerId);


        } catch (Exception ex) {//예외 발생시 처리
            throw new ServiceMethodException("getMemberFavoriteNovels Error");
        }


    }
}
