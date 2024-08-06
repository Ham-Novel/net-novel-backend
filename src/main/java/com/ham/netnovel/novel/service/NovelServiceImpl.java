package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelInfoDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;


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


    @Override
    @Transactional(readOnly = true)
    public Optional<Novel> getNovel(Long novelId) {
        return novelRepository.findById(novelId);
    }

    @Override
    public List<Novel> getNovelsByAuthor(String providerId) {
        return novelRepository.findNovelsByMember(providerId);
    }

    @Override
    @Transactional
    public void createNovel(NovelCreateDto novelCreateDto) {
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
            novelRepository.save(targetNovel);
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovel 메서드 에러 발생", ex.getCause());
        }

    }

    @Override
    @Transactional
    public void updateNovel(NovelUpdateDto novelUpdateDto) {
        log.info("Novel 변경 = {}", novelUpdateDto.toString());

        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelUpdateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelUpdateDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 없습니다.");
            }

            //updateDto에서 특정 property 값만 업데이트하는 로직.
            //null 값과 빈 String은 제외시키고 update 작업을 수행.
            if (novelUpdateDto.getTitle() != null && !novelUpdateDto.getTitle().isBlank()) {
                targetNovel.updateTitle(novelUpdateDto.getTitle());
            }
            if (novelUpdateDto.getDescription() != null && !novelUpdateDto.getDescription().isBlank()) {
                targetNovel.updateDesc(novelUpdateDto.getDescription());
            }
            if (novelUpdateDto.getType() != null) {
                targetNovel.updateType(novelUpdateDto.getType());
            }

            novelRepository.save(targetNovel);
        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteNovel(NovelDeleteDto novelDeleteDto) {
        log.info("Novel 삭제 = {}", novelDeleteDto.toString());

        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelDeleteDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelDeleteDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 업습니다.");
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("deleteNovel 메서드 에러 발생", ex.getCause());
        }

        //Novel 삭제 처리
        targetNovel.changeStatus(NovelStatus.DELETED_BY_USER);
        novelRepository.save(targetNovel);
    }

    @Override
    @Transactional(readOnly = true)
    public NovelInfoDto getNovelInfo(Long novelId) {
        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            return convertEntityToInfoDto(targetNovel);
        } catch (Exception ex) {
            throw new ServiceMethodException("getNovelInfo 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Novel> getNovels(Pageable pageable) {
        return null;
    }

    //단순히 엔티티 List만 반환하는 메서드
    //Null체크, DTO 변환은 MemberMyPageService에서 진행
    @Override
    @Transactional(readOnly = true)
    public List<Novel> getFavoriteNovels(String providerId) {
        try {
            return novelRepository.findFavoriteNovelsByMember(providerId);
        } catch (Exception ex) {//예외 발생시 처리
            throw new ServiceMethodException("getFavoriteNovels 메서드 에러 발생", ex.getCause());
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getRatedNovelIds() {
        try {
            return novelRepository.findByNovelRating()
                    .stream()
                    .map(Novel::getId)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            throw new ServiceMethodException("getRatedNovelIds 메서드 에러 발생", ex.getCause());

        }
    }

    NovelInfoDto convertEntityToInfoDto(Novel novel) {
        return NovelInfoDto.builder()
                .novelId(novel.getId())
                .title(novel.getTitle())
                .description(novel.getDescription())
                .authorName(novel.getAuthor().getNickName())
                .views(novel.getEpisodes().stream().mapToInt(Episode::getView).sum())
                .episodeCount(novel.getEpisodes().size())
                //Todo FavoriteNovel, NovelTag 도메인 완성되는 대로 작업
                .favoriteCount(0)
                .tags(Collections.emptyList())
                .build();
    }
}
