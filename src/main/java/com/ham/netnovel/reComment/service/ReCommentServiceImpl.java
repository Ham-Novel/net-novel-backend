package com.ham.netnovel.reComment.service;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.Service.MemberService;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.ReCommentRepository;
import com.ham.netnovel.reComment.dto.ReCommentCreateDto;
import com.ham.netnovel.reComment.dto.ReCommentDeleteDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import com.ham.netnovel.reComment.dto.ReCommentUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ReCommentServiceImpl implements ReCommentService {

    private final ReCommentRepository reCommentRepository;
    private final MemberService memberService;
    private final CommentService commentService;

    public ReCommentServiceImpl(ReCommentRepository reCommentRepository, MemberService memberService, CommentService commentService) {
        this.reCommentRepository = reCommentRepository;
        this.memberService = memberService;
        this.commentService = commentService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReComment> getReComment(Long reCommentId) {
        return reCommentRepository.findById(reCommentId);
    }

    @Override
    @Transactional
    public void createReComment(ReCommentCreateDto reCommentCreateDto) {
        log.info("댓글정보={}", reCommentCreateDto.toString());
        //Member 엔티티 조회, null 이면 예외로 던짐
        Member member = memberService.getMember(reCommentCreateDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("Member 정보 없음"));


        //comment 엔티티 조회, null 이면 예외로 던짐

        Comment comment = commentService.getComment(reCommentCreateDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("comment 정보 없음"));
        try {


            //ReComment 엔티티 생성
            ReComment reComment = new ReComment(reCommentCreateDto.getContent(), comment, member);

            //ReComment 엔티티 저장
            reCommentRepository.save(reComment);


        } catch (Exception ex) {

            throw new ServiceMethodException("createReComment 메서드 에러 발생"); // 예외 던지기
        }


    }

    @Override
    @Transactional
    public void updateReComment(ReCommentUpdateDto reCommentUpdateDto) {
        //null 체크, null이면 예외로 던짐
        ReComment reComment = getReComment(reCommentUpdateDto.getReCommentId())
                .orElseThrow(() -> new RuntimeException("잘못된요청"));
        try {

            //대댓글 작성한 유저 정보
            String providerId = reComment.getMember().getProviderId();
            //댓글의 Id 값
            Long commentId = reComment.getComment().getId();
            //댓글 수정 요청자와 댓글 작성자가 일치하는지 확인
            //댓글 수정 에피소드와 기존 댓글과 mapping된 에피소드가 일치하는지 확인
            if (providerId.equals(reCommentUpdateDto.getProviderId()) && Objects.equals(commentId, reCommentUpdateDto.getCommentId())) {
                reComment.updateReComment(reCommentUpdateDto.getContent());
                reCommentRepository.save(reComment);

            } else {
                throw new IllegalArgumentException("updateReComment 메서드 에러 발생, 잘못된 파라미터 입력");
            }
        } catch (Exception ex) {
            //그외 예외처리
            throw new ServiceMethodException("updateReComment 메서드 에러 발생"); // 예외 던지기
        }


    }

    @Override
    @Transactional
    public void deleteReComment(ReCommentDeleteDto commentDeleteDto) {
        ReComment reComment = getReComment(commentDeleteDto.getReCommentId())
                .orElseThrow(() -> new RuntimeException("잘못된요청"));
        try {
            //대댓글 작성한 유저 정보
            String providerId = reComment.getMember().getProviderId();
            //댓글의 Id 값
            Long commentId = reComment.getComment().getId();

            //대댓글 삭제 요청자와 댓글 작성자가 일치하는지 확인
            //대댓글 삭제 댓글 mapping된 댓글이 일치하는지 확인
            if (providerId.equals(commentDeleteDto.getProviderId()) && Objects.equals(commentId, commentDeleteDto.getCommentId())) {

                //엔티티 상태 변경
                reComment.changeReStatus(CommentStatus.DELETED_BY_USER);

                //상태 변경한 엔티티 저장
                reCommentRepository.save(reComment);

            } else {
                throw new IllegalArgumentException("deleteReComment 메서드 에러 발생, 잘못된 파라미터 입력");
            }


        } catch (Exception ex) {
            log.error("Failed to delete comment to database: {}", ex.getMessage());
            throw new ServiceMethodException("deleteReComment 메서드 에러 발생"); // 예외 던지기


        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<ReCommentListDto> getReCommentList(Long commentId) {
        try {
            return reCommentRepository.findByCommentId(commentId)
                    .stream().map(reComment -> ReCommentListDto.builder()
                            .nickName(reComment.getMember().getNickName())
                            .content(reComment.getContent())
                            .reCommentId(reComment.getComment().getId())
                            .updatedAt(reComment.getUpdatedAt())
                            .build()).toList();


        } catch (Exception e) {
            throw new ServiceMethodException("getReCommentList 메서드 에러 발생"); // 예외 던지기

        }

    }
}
