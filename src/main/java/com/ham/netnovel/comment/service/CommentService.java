package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;

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

}
