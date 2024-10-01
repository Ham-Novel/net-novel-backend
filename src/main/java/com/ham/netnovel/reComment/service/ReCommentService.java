package com.ham.netnovel.reComment.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.dto.ReCommentCreateDto;
import com.ham.netnovel.reComment.dto.ReCommentDeleteDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import com.ham.netnovel.reComment.dto.ReCommentUpdateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface ReCommentService {

    /**
     * reCommentId 값으로 DB에서 정보를 가져오는 메서드, 사용시 Null체크 필수
     * @param reCommentId
     * @return
     */
    Optional<ReComment> getReComment(Long reCommentId);

    /**
     * 대댓글을 생성하는 메서드입니다.
     *
     * <p>주어진 {@link ReCommentCreateDto} 정보를 바탕으로
     * Member 엔티티와 Comment 엔티티 조회후, 이상이 없으면 대댓글을 생성하고 저장합니다.</p>
     *
     * @param reCommentCreateDto 대댓글 생성에 필요한 정보를 담고 있는 {@link ReCommentCreateDto} 객체
     * @throws NoSuchElementException 주어진 providerId 또는 commentId에 해당하는
     *         회원이나 댓글 정보가 존재하지 않을 경
     * @throws ServiceMethodException 서비스 메서드 처리 중 에러가 발생한 경우
     */
    void createReComment(ReCommentCreateDto reCommentCreateDto);


    /**
     * 대댓글의 내용을 업데이트하는 메서드 입니다.
     * <p> 대댓글이 DB에 존재하는지 여부와
     * 해당 대댓글을 작성한 유저와 수정 요청 유저가 동일한지 확인한 후,
     * 대댓글의 내용을 변경후 DB에 저장합니다. </p>
     *
     ** @param reCommentUpdateDto 업데이트할 대댓글의 정보가 담긴 {@link ReCommentUpdateDto} 객체
     * @throws NoSuchElementException 대댓글 ID에 해당하는 대댓글이 없을 경우 발생
     * @throws IllegalArgumentException 대댓글 작성자와 수정 요청자가 다르거나, 댓글 ID가 일치하지 않을 경우 발생
     * @throws ServiceMethodException 데이터베이스 저장 중 오류가 발생한 경우 발생
     */
    void updateReComment(ReCommentUpdateDto reCommentUpdateDto);


    /**
     * 대댓글을 삭제 상태로 변경하는 메서드입니다.
     *
     * <p>주어진 {@link ReCommentDeleteDto} 정보를 바탕으로,
     * 대댓글이 DB에 존재하는지 여부와
     * 해당 대댓글을 작성한 유저와 삭제 요청 유저가 동일한지 확인한 후,
     * 대댓글의 상태를 삭제된 상태로 변경후 DB에 저장합니다. </p>
     *
     *
     ** @param commentDeleteDto 삭제상태로 변경할 대댓글의 정보가 담긴 {@link ReCommentUpdateDto} 객체
     * @throws NoSuchElementException 대댓글 ID에 해당하는 대댓글이 없을 경우 발생
     * @throws IllegalArgumentException 대댓글 작성자와 삭제 요청자가 다르거나, 댓글 ID가 일치하지 않을 경우 발생
     * @throws ServiceMethodException 데이터베이스 저장 중 오류가 발생한 경우 발생
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
    List<MemberCommentDto> getMemberReCommentList(String providerId, Pageable pageable);



}
