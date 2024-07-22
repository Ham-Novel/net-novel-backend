package com.ham.netnovel.reComment.service;

import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.dto.ReCommentCreateDto;
import com.ham.netnovel.reComment.dto.ReCommentDeleteDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import com.ham.netnovel.reComment.dto.ReCommentUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ReCommentService {

    /**
     * reCommentId 값으로 DB에서 정보를 가져오는 메서드, 사용시 Null체크 필수
     * @param reCommentId
     * @return
     */
    Optional<ReComment> getReComment(Long reCommentId);

    /**
     * 유저가 작성한 대댓글을 DB에 저장하는 메서드
     * @param reCommentCreateDto
     */
    void createReComment(ReCommentCreateDto reCommentCreateDto);


    /**
     * 대댓글을 수정하는 메서드
     * @param reCommentUpdateDto
     */
    void updateReComment(ReCommentUpdateDto reCommentUpdateDto);


    /**
     * 댓글의 상태를 삭제상태로 바꾸는 메서드
     * @param commentDeleteDto 삭제 요청된 댓글(comment)의 정보를 담는 DTO
     */
    void deleteReComment(ReCommentDeleteDto commentDeleteDto);


    /**
     * 댓글 달린 대댓글들을 List로 반환하는 메서드
     * @param commentId comment의 PK값
     * @return ReCommentListDto List 형태로 반환
     */
    List<ReCommentListDto> getReCommentList(Long commentId);


    /**
     * 유저가 작성한 대댓글을 DB에서 찾아 반환하는 메서드
     * @param providerId
     * @return
     */
    List<MemberCommentDto> getMemberReCommentList(String providerId);



}
