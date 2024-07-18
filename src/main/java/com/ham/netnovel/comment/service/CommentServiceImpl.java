package com.ham.netnovel.comment.service;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentRepository;
import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.Service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;

    private final MemberService memberService;

    private final EpisodeService episodeService;

    public CommentServiceImpl(CommentRepository commentRepository, MemberService memberService, EpisodeService episodeService) {
        this.commentRepository = commentRepository;
        this.memberService = memberService;
        this.episodeService = episodeService;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    @Override
    public void createComment(CommentCreateDto commentCreateDto) {

        log.info("댓글정보={}", commentCreateDto.toString());
        try {
            //Member 엔티티 조회, null이면 예외로 던짐
            Member member = memberService.getMember(commentCreateDto.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Member 정보 없음"));

            //Member 엔티티 조회, null이면 예외로 던짐
            Episode episode = episodeService.getEpisode(commentCreateDto.getEpisodeId())
                    .orElseThrow(() -> new RuntimeException("episode 정보 없음"));


            //Comment 엔티티 생성
            Comment comment = new Comment(commentCreateDto.getContent(), episode, member);


            //Comment 엔티티 저장
            commentRepository.save(comment);


        } catch (DataAccessException ex) {
            // 데이터베이스 저장 과정에서 예외 발생 시 처리
            log.error("Failed to save comment to database: {}", ex.getMessage());
            throw new RuntimeException("Comment 생성 에러 발생"); // 예외 던지기
        } catch (Exception ex) {
            //그외 예외처리
            log.error("Failed to save comment to database: {}", ex.getMessage());
            throw new RuntimeException("Failed to save comment. Please try again later."); // 예외 던지기
        }
    }

    //ToDo 예외처리 구체적으로 작성
    @Override
    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto) {
        log.info("댓글정보={}", commentUpdateDto.toString());

        try {
            //null 체크, null이면 예외로 던짐
            Comment comment = getComment(commentUpdateDto.getCommentId())
                    .orElseThrow(() -> new RuntimeException("잘못된요청"));

            String providerId = comment.getMember().getProviderId();

            Long episodeId = comment.getEpisode().getId();


            //댓글 수정 요청자와 댓글 작성자가 일치하는지 확인
            //댓글 수정 에피소드와 기존 댓글과 mapping된 에피소드가 일치하는지 확인
            if (providerId.equals(commentUpdateDto.getProviderId()) && Objects.equals(episodeId, commentUpdateDto.getEpisodeId())) {
                comment.updateComment(commentUpdateDto.getContent());
                commentRepository.save(comment);

            } else {
                throw new RuntimeException("댓글 삭제 에러, 잘못된 유저의 요청");
            }

        } catch (Exception ex) {
            //그외 예외처리
            log.error("Failed to save comment to database: {}", ex.getMessage());
            throw new RuntimeException("Failed to save comment. Please try again later."); // 예외 던지기
        }


    }

    @Override
    @Transactional
    public void deleteComment(CommentDeleteDto commentDeleteDto) {

        try {
            //null 체크, null이면 예외로 던짐
            Comment comment = getComment(commentDeleteDto.getCommentId())
                    .orElseThrow(() -> new RuntimeException("잘못된요청"));

            String providerId = comment.getMember().getProviderId();

            Long episodeId = comment.getEpisode().getId();

            //댓글 삭제 요청자와 댓글 작성자가 일치하는지 확인
            //댓글 삭제 에피소드와 기존 댓글과 mapping된 에피소드가 일치하는지 확인
            if (providerId.equals(commentDeleteDto.getProviderId()) && Objects.equals(episodeId, commentDeleteDto.getEpisodeId())) {

                //엔티티의 상태를 삭제 상태로 변경
                comment.changeStatus(CommentStatus.DELETED_BY_USER);

                //변경된 엔티티를 저장
                commentRepository.save(comment);

            } else {
                throw new RuntimeException("에러");
            }

        } catch (Exception ex) {
            //그외 예외처리
            log.error("Failed to delete comment to database: {}", ex.getMessage());
            throw new RuntimeException("Failed to save comment. Please try again later."); // 예외 던지기
        }


    }

}
