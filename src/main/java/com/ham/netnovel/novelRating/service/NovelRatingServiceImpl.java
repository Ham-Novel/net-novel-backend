package com.ham.netnovel.novelRating.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelRating.*;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class NovelRatingServiceImpl implements NovelRatingService {

    private final NovelRatingRepository novelRatingRepository;

    private final MemberService memberService;

    private final NovelService novelService;

    public NovelRatingServiceImpl(NovelRatingRepository novelRatingRepository, MemberService memberService, NovelService novelService) {
        this.novelRatingRepository = novelRatingRepository;
        this.memberService = memberService;
        this.novelService = novelService;
    }

    @Override
    @Transactional
    public void saveNovelRating(NovelRatingSaveDto novelRatingSaveDto) {
        //멤버 엔티티 조회, 없을경우 예외로 던짐
        Member member = memberService.getMember(novelRatingSaveDto.getProviderId())
                .orElseThrow(() ->
                        new NoSuchElementException("saveNovelRating 메서드 에러, 유저 정보가 null입니다. providerId=" + novelRatingSaveDto.getProviderId()));

        Novel novel = novelService.getNovelEntity(novelRatingSaveDto.getNovelId())
                .orElseThrow(() ->
                        new NoSuchElementException("saveNovelRating 메서드 에러, 유저 정보가 null입니다. providerId=" + novelRatingSaveDto.getNovelId()));

        //타입 체크, null 이거나 범위(1~10)에서 벗어나면 예외로 던져짐
        TypeValidationUtil.validateNovelRating(novelRatingSaveDto.getRating());

        try {
            //EpisodeRating embedded key 생성
            NovelRatingId id = new NovelRatingId(member.getId(), novel.getId());
            //embedded key 로 EpisodeRating 조회
            Optional<NovelRating> optionalNovelRating = novelRatingRepository.findById(id);
            //조회된 엔티티가 없으면, DB에 엔티티 저장
            if (optionalNovelRating.isEmpty()) {

                NovelRating newNovelRating = NovelRating.builder()
                        .rating(novelRatingSaveDto.getRating())
                        .novel(novel)
                        .member(member)
                        .id(id)
                        .build();
                //엔티티 저장
                novelRatingRepository.save(newNovelRating);


            }
            //조회된 엔티티가 있으면, 엔티티의 별점 필드값 수정 후 DB에 엔티티 저장
            else {
                //Optional 벗기기
                NovelRating novelRating = optionalNovelRating.get();
                //엔티티 별점 점수 변경
                novelRating.updateRating(novelRatingSaveDto.getRating());
                //변경된 엔티티 DB에 저장
                novelRatingRepository.save(novelRating);
            }
        } catch (Exception ex) {
            // 그 외의 예외는 ServiceMethodException으로 래핑하여 던짐
            throw new ServiceMethodException("saveEpisodeRating 메서드 에러 발생" + ex.getMessage());
        }

    }


}

