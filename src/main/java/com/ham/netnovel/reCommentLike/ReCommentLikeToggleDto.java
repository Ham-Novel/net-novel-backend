package com.ham.netnovel.reCommentLike;


import com.ham.netnovel.commentLike.LikeType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentLikeToggleDto {
    private String providerId;//유저정보


    @NotNull(message = "좋아요 정보가 없습니다.")
    private LikeType likeType;//좋아요/싫어요 선택 정보

    @NotNull(message = "댓글 정보가 없습니다.")
    private Long reCommentId;//좋아요 누른 대댓글의 PK 정보
}
