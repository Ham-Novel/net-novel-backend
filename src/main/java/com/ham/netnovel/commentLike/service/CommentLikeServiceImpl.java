package com.ham.netnovel.commentLike.service;

import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.commentLike.CommentLike;
import com.ham.netnovel.commentLike.CommentLikeKey;
import com.ham.netnovel.commentLike.CommentLikeRepository;
import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final MemberService memberService;
    private final CommentLikeRepository commentLikeRepository;

    private final CommentService commentService;

    public CommentLikeServiceImpl(MemberService memberService, CommentLikeRepository commentLikeRepository, CommentService commentService) {
        this.memberService = memberService;
        this.commentLikeRepository = commentLikeRepository;
        this.commentService = commentService;
    }

    @Override
    @Transactional
    public boolean toggleCommentLikeStatus(CommentLikeToggleDto commentLikeToggleDto) {

        //멤버 엔티티 조회, 없을경우 예외로 던짐
        Member member = memberService.getMember(commentLikeToggleDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("toggleCommentLikeStatus 메서드 에러, 유저 정보가 null입니다. providerId=" + commentLikeToggleDto.getProviderId()));

        //댓글 엔티티 조회, 없을경우 예외로 던짐
        Comment comment = commentService.getComment(commentLikeToggleDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("toggleCommentLikeStatus 메서드 에러, 댓글 정보가 null입니다. commentId=" + commentLikeToggleDto.getCommentId()));

        try {
            //CommentLike 엔티티 조회를 위한 composite key 생성
            CommentLikeKey commentLikeKey = new CommentLikeKey(comment.getId(), member.getId());

            //DB에서 엔티티 찾아서 반환
            Optional<CommentLike> commentLike = commentLikeRepository.findById(commentLikeKey);

            //찾은 값이 없으면(좋아요 누른 기록이 없음), 새로운 엔티티 만들어 DB에 저장 후 true 반환
            if (commentLike.isEmpty()) {

                CommentLike newCommentLike = new CommentLike(commentLikeKey, member, comment, commentLikeToggleDto.getLikeType());

                commentLikeRepository.save(newCommentLike);

                return true;
            } else {
                //찾은 값이 있으면(좋아요 누른 기록이 있음) 좋아요 기록 삭제, false 반환
                commentLikeRepository.delete(commentLike.get());

                return false;
            }

        } catch (Exception ex) {
            // 그 외의 예외는 ServiceMethodException으로 래핑하여 던짐
            throw new ServiceMethodException("toggleCommentLikeStatus 메서드 에러 발생" + ex.getMessage());
        }


    }
}
