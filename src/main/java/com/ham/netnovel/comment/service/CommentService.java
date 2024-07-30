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
     * 유저가 작성한 댓글을 DB에서 찾아 반환하는 메서드
     * @param providerId
     * @return
     */
    List<MemberCommentDto> getMemberCommentList(String providerId);



    /**
     * Episode에 달린 댓글과 대댓글을 최신순으로 반환하는 메서드
     * @param episodeId episode의 PK값
     * @return CommentEpisodeListDto List 형태로 반환
     */
    List<CommentEpisodeListDto> getEpisodeCommentListByRecent(Long episodeId);


    /**
     * Episode에 달린 댓글과 대댓글을 좋아요 순으로 반환하는 메서드
     * @param episodeId episode의 PK값
     * @return CommentEpisodeListDto List 형태로 반환
     */
    List<CommentEpisodeListDto> getEpisodeCommentListByLikes(Long episodeId);



    /**
     * Novel에 달린 댓글과 대댓글을 최신순으로 반환하는 메서드
     * @param novelId Novel 의 PK 값
     * @return List 댓글과 대댓글 정보를 담은 DTO List
     */
    List<CommentEpisodeListDto> getNovelCommentListByRecent(Long novelId);

    /**
     * Novel에 달린 댓글과 대댓글을 좋아요 순으로 반환하는 메서드
     * @param novelId Novel 의 PK 값
     * @return List 댓글과 대댓글 정보를 담은 DTO List
     */
    List<CommentEpisodeListDto> getNovelCommentListByLikes(Long novelId);





}
