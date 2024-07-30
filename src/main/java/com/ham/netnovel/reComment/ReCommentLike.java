package com.ham.netnovel.reComment;

import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.reCommentLike.ReCommentLikeKey;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReCommentLike {

    @EmbeddedId
    private ReCommentLikeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // 복합 키의 memberId 필드와 매핑
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reCommentId")// 복합 키의 reCommentId 필드와 매핑
    @JoinColumn(name="re_comment_id")
    private ReComment reComment;

    //LIKE, DISLIKE 두가지 타입
    @Enumerated(EnumType.STRING)
    private LikeType likeType;

    @Builder
    public ReCommentLike(ReCommentLikeKey id, Member member, ReComment reComment, LikeType likeType) {
        this.id = id;
        this.member = member;
        this.reComment = reComment;
        this.likeType = likeType;
    }
}
