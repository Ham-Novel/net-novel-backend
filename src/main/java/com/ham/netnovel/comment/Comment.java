package com.ham.netnovel.comment;


import com.ham.netnovel.commentLike.CommentLike;
import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.reComment.ReComment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 에피소드별 댓글 엔티티
 */
@Entity
@NoArgsConstructor
@Getter
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //댓글 내용 300자 제한이나, DB 유연성을 위해 305자로 설정
    @Column(nullable = false,length = 305)
    private String content;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private  CommentStatus status = CommentStatus.ACTIVE;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



    @OneToMany(mappedBy = "comment")
    private List<ReComment> reComments = new ArrayList<>();

    //N인 comment에서 외래키 가져감
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;

    //작성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<CommentLike> commentLikes= new ArrayList<>();






    @Builder
    public Comment(String content, Episode episode, Member member) {
        this.content =content;
        this.episode = episode;
        this.member = member;

    }

    //댓글 엔티티 내용 변경
    public void updateComment(String content){
        this.content = content;
    }

    //댓글 엔티티 상태 변경
   public  void changeStatus(CommentStatus status) {
        this.status = status;
    }



    //좋아요 수를 반환하는 메서드
    public int getTotalLikes(){
        return (int) commentLikes.stream()
                .filter(commentLike -> commentLike.getLikeType() == LikeType.LIKE)
                .count();

    }
    //싫어요 수를 반환하는 메서드
    public int getTotalDisLikes(){
        return (int) commentLikes.stream()
                .filter(commentLike -> commentLike.getLikeType() == LikeType.DISLIKE)
                .count();

    }



}
