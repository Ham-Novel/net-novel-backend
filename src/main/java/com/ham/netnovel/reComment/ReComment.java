package com.ham.netnovel.reComment;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.commentLike.data.LikeType;
import com.ham.netnovel.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class ReComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //대댓글 내용 300자 제한이나, DB 유연성을 위해 305자로 설정
    @Column(nullable = false,length = 305)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.ACTIVE;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //N인 reComment에서 외래키 가져감
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    //작성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reComment", fetch = FetchType.LAZY)
    private List<ReCommentLike>  reCommentLikes =new ArrayList<>();

    @Builder
    public ReComment(String content, Comment comment, Member member) {
        this.content = content;
        this.comment = comment;
        this.member = member;
    }

    //대댓글 엔티티 내용 변경
    public void updateReComment(String content){
        this.content = content;
    }

    //대댓글 엔티티 상태 변경
    public  void changeReStatus(CommentStatus status) {
        this.status = status;
    }

    //좋아요 수를 반환하는 메서드
    public int getTotalLikes(){
        return (int) reCommentLikes.stream()
                .filter(commentLike -> commentLike.getLikeType() == LikeType.LIKE)
                .count();

    }
    //싫어요 수를 반환하는 메서드
    public int getTotalDisLikes(){
        return (int) reCommentLikes.stream()
                .filter(commentLike -> commentLike.getLikeType() == LikeType.DISLIKE)
                .count();

    }




}
