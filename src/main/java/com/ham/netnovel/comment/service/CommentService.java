package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.data.CommentSortOrder;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.dto.MemberCommentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {


    /**
     * commentId 값으로 DB에서 정보를 가져오는 메서드, 사용시 Null체크 필수
     * @param commentId 댓글의 ID 값
     * @return Optional Comment 엔티티 Optional로 감싸 반환
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
     * @param providerId 유저 정보
     * @return
     */
    List<MemberCommentDto> getMemberCommentList(String providerId,Pageable pageable);


    /**
     * 주어진 에피소드에 대한 댓글 목록을 정렬 기준에 따라 가져옵니다.
     *
     * <p>
     * 정렬 기준은 `최신순` 또는 `추천순` 이며, 이를 기준으로 댓글을 가져옵니다.
     * 댓글 작성자와 접속유저가 동일하면 DTO의 <b>isEditAble</b> 값을 true로 반환합니다.
     * </p>
     *
     * @param episodeId  댓글을 가져올 에피소드의 ID
     * @param pageable   페이징 정보
     * @param providerId 접속한 유저의 정보, 로그인을 안한경우 <b>NON_LOGIN</b> 할당
     * @param sortOrder  댓글을 정렬할 기준 (예: 최신순, 좋아요순)
     * @return 에피소드 댓글정보를 담는 {@link CommentEpisodeListDto} 객체 리스트
     * @throws ServiceMethodException 댓글을 가져오는 중 오류가 발생한 경우
     */
    List<CommentEpisodeListDto> getEpisodeComment(Long episodeId, Pageable pageable, String providerId, CommentSortOrder sortOrder);


    /**
     * Novel에 달린 댓글과 대댓글을 최신순으로 반환하는 메서드
     * @param novelId Novel 의 PK 값
     * @return List 댓글과 대댓글 정보를 담은 DTO List
     */
    List<CommentEpisodeListDto> getNovelCommentListByRecent(Long novelId,Pageable pageable);


    /**
     * Novel에 달린 댓글과 대댓글을 좋아요 순으로 반환하는 메서드
     * @param novelId Novel 의 PK 값
     * @return List 댓글과 대댓글 정보를 담은 DTO List
     */
    List<CommentEpisodeListDto> getNovelCommentListByLikes(Long novelId, Pageable pageable);





}
