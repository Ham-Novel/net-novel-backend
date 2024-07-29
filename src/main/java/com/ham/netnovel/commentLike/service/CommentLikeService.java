package com.ham.netnovel.commentLike.service;

import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;

public interface CommentLikeService {



    /**
     * 댓글의 좋아요 상태를 저장하는 메서드
     * 좋아요 누른 기록이 없으면, 새로운 엔티티를 만들어 DB에 저장 후 true 반환
     * 좋아요 누른 기록이 있으면, DB에서 정보 삭제 후 false 반환
     * @param commentLikeToggleDto 유저 정보, 댓글정보, 좋아요타입을 멤버변수로 갖는 DTO
     * @return boolean 좋아요 상태 저장시 true, 삭제시 false 반환
     */
    boolean toggleCommentLikeStatus(CommentLikeToggleDto commentLikeToggleDto);

}
