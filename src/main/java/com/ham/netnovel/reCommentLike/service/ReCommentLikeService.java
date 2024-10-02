package com.ham.netnovel.reCommentLike.service;

import com.ham.netnovel.commentLike.data.LikeResult;
import com.ham.netnovel.reCommentLike.ReCommentLikeToggleDto;

public interface ReCommentLikeService {



    LikeResult toggleReCommentLikeStatus(ReCommentLikeToggleDto reCommentLikeToggleDto);

}
