package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.member.dto.MemberCommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {


    /**
     * commentId 값으로 DB에서 정보를 가져오는 메서드, 사용시 Null체크 필수
     * @param commentId
     * @return
     */
    Optional<Comment> getComment(Long commentId);

    /**
     * 유저가 작성한 댓글을 DB에 저장하는 메서드
     *
     * @param commentCreateDto
     */
    void createComment(CommentCreateDto commentCreateDto);


    /**
     * 댓글을 수정하는 메서드
     * @param commentUpdateDto
     */
    void updateComment(CommentUpdateDto commentUpdateDto);


    /**
     * 댓글의 상태를 삭제상태로 바꾸는 메서드
     * @param commentDeleteDto 삭제 요청된 댓글(comment)의 정보를 담는 DTO
     */
    void deleteComment(CommentDeleteDto commentDeleteDto);


    /**
     * Episode에 달린 댓글들을 List로 반환하는 메서드
     * @param episodeId episode의 PK값
     * @return CommentEpisodeListDto List 형태로 반환
     */
    List<CommentEpisodeListDto> getEpisodeCommentList(Long episodeId);


    /**
     * 유저가 작성한 댓글을 DB에서 찾아 반환하는 메서드
     * @param providerId
     * @return
     */
    List<MemberCommentDto> getMemberCommentList(String providerId);






}
