package com.ham.netnovel.reCommentLike.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.ReCommentLike;
import com.ham.netnovel.reComment.service.ReCommentService;
import com.ham.netnovel.reCommentLike.ReCommentLikeKey;
import com.ham.netnovel.reCommentLike.ReCommentLikeRepository;
import com.ham.netnovel.reCommentLike.ReCommentLikeToggleDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReCommentLikeServiceImpl implements ReCommentLikeService {
    private final MemberService memberService;

    private final ReCommentService reCommentService ;
    private final ReCommentLikeRepository reCommentLikeRepository;

    public ReCommentLikeServiceImpl(MemberService memberService, ReCommentService reCommentService, ReCommentLikeRepository reCommentLikeRepository) {
        this.memberService = memberService;
        this.reCommentService = reCommentService;
        this.reCommentLikeRepository = reCommentLikeRepository;
    }

    @Override
    @Transactional
    public boolean toggleReCommentLikeStatus(ReCommentLikeToggleDto dto) {
        //멤버 엔티티 조회, 없을경우 예외로 던짐
        Member member = memberService.getMember(dto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("toggleReCommentLikeStatus 메서드 에러, 유저 정보가 null입니다. providerId=" + dto.getProviderId()));


        ReComment reComment = reCommentService.getReComment(dto.getReCommentId())
                .orElseThrow(() -> new NoSuchElementException("toggleReCommentLikeStatus 메서드 에러, 댓글 정보가 null입니다. commentId=" + dto.getReCommentId()));

        try {
            //ReCommentLike 엔티티 조회를 위한 composite key 생성
            ReCommentLikeKey reCommentLikeKey = new ReCommentLikeKey(reComment.getId(), member.getId());
            //DB에서 엔티티 찾아서 반환
            Optional<ReCommentLike> reCommentLike = reCommentLikeRepository.findById(reCommentLikeKey);

            //찾은 값이 없으면(좋아요 누른 기록이 없음), 새로운 엔티티 만들어 DB에 저장 후 true 반환
            if (reCommentLike.isEmpty()){
                ReCommentLike newRecommentLike = new ReCommentLike(reCommentLikeKey, member, reComment, dto.getLikeType());

                reCommentLikeRepository.save(newRecommentLike);

                return true;

            }else {
                //찾은 값이 있으면(좋아요 누른 기록이 있음) 좋아요 기록 삭제, false 반환
                reCommentLikeRepository.delete(reCommentLike.get());
                return false;

            }


        }catch (Exception ex) {
            // 그 외의 예외는 ServiceMethodException으로 래핑하여 던짐
            throw new ServiceMethodException("toggleReCommentLikeStatus 메서드 에러 발생" + ex.getMessage());
        }
    }
}
