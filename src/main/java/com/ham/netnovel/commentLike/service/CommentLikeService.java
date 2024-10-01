package com.ham.netnovel.commentLike.service;

import com.ham.netnovel.commentLike.data.LikeResult;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.common.exception.ServiceMethodException;

import java.util.NoSuchElementException;

public interface CommentLikeService {



    /**
     * 댓글에 대한 좋아요 또는 싫어요 상태를 전환하는 메소드입니다.
     *
     * 이 메소드는 주어진 댓글 ID와 사용자 ID를 기반으로 댓글 감정 표현을 추가하거나 삭제합니다.
     *
     * @param commentLikeToggleDto 댓글,대댓글,유저정보를 전달하는 {@link CommentLikeToggleDto} 객체
     * @return LikeResult          감정 표현 상태를 나타내는 열거형 값 {@code CREATION} {@code DELETION} {@code FAILURE} 중 하나
     * @throws NoSuchElementException 주어진 providerId 또는 commentId에 해당하는 멤버 또는 댓글을 찾을 수 없는 경우
     * @throws ServiceMethodException 서비스 메서드 처리 중 에러가 발생한 경우
     */
    LikeResult toggleCommentLikeStatus(CommentLikeToggleDto commentLikeToggleDto);

}
