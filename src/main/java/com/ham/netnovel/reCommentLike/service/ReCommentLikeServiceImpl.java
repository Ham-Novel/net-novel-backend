package com.ham.netnovel.reCommentLike.service;

import com.ham.netnovel.commentLike.data.LikeResult;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.ReCommentLike;
import com.ham.netnovel.reComment.service.ReCommentService;
import com.ham.netnovel.reCommentLike.ReCommentLikeId;
import com.ham.netnovel.reCommentLike.ReCommentLikeRepository;
import com.ham.netnovel.reCommentLike.ReCommentLikeToggleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class ReCommentLikeServiceImpl implements ReCommentLikeService {
    private final MemberService memberService;

    private final ReCommentService reCommentService;
    private final ReCommentLikeRepository reCommentLikeRepository;

    public ReCommentLikeServiceImpl(MemberService memberService, ReCommentService reCommentService, ReCommentLikeRepository reCommentLikeRepository) {
        this.memberService = memberService;
        this.reCommentService = reCommentService;
        this.reCommentLikeRepository = reCommentLikeRepository;
    }

    @Override
    @Transactional
    public LikeResult toggleReCommentLikeStatus(ReCommentLikeToggleDto dto) {
        //멤버 엔티티 조회, 없을경우 예외로 던짐
        Member member = memberService.getMember(dto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("toggleReCommentLikeStatus 메서드 에러, 유저 정보가 null입니다. providerId=" + dto.getProviderId()));

        ReComment reComment = reCommentService.getReComment(dto.getReCommentId())
                .orElseThrow(() -> new NoSuchElementException("toggleReCommentLikeStatus 메서드 에러, 댓글 정보가 null입니다. commentId=" + dto.getReCommentId()));

        try {
            //ReCommentLike 엔티티 조회를 위한 composite key 생성
            ReCommentLikeId reCommentLikeId = new ReCommentLikeId(reComment.getId(), member.getId());
            //DB에서 엔티티 찾아서 반환
            Optional<ReCommentLike> reCommentLike = reCommentLikeRepository.findById(reCommentLikeId);

            //찾은 값이 없으면(좋아요 누른 기록이 없음), 새로운 엔티티 만들어 DB에 저장 후 true 반환
            if (reCommentLike.isEmpty()) {

                //새로운 대댓글 감정 엔티티 생성
                ReCommentLike newReCommentLike = ReCommentLike.builder()
                        .likeType(dto.getLikeType())
                        .id(reCommentLikeId)
                        .reComment(reComment)
                        .member(member)
                        .build();
                //DB에 저장
                reCommentLikeRepository.save(newReCommentLike);
                log.info("대댓글 감정 등록 완료, memberId={}, commentId={}", member.getId(), reComment.getId());
                return LikeResult.CREATION;//생성상태 반환

            } else if (reCommentLike.get().getLikeType().equals(dto.getLikeType())) {
                //찾은 값이 있으면(좋아요 누른 기록이 있음) 좋아요 기록 삭제, false 반환
                reCommentLikeRepository.delete(reCommentLike.get());
                log.info("대댓글 감정 삭제 완료, memberId={}, commentId={}", member.getId(), reComment.getId());
                return LikeResult.DELETION;//삭제상태 반환
            } else {
                log.warn("toggleReCommentLikeStatus 메서드 경고, 기존 대댓글 감정표현과 요청된 감정표현이 다릅니다." +
                        " memberId={}, commentId={}", member.getId(), reComment.getId());
                return LikeResult.FAILURE;//실패상태 반환

            }


        } catch (Exception ex) {
            // 그 외의 예외는 ServiceMethodException으로 래핑하여 던짐
            throw new ServiceMethodException("toggleReCommentLikeStatus 메서드 에러 발생" + ex.getMessage());
        }
    }
}
