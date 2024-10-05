package com.ham.netnovel.member.dto;


import com.ham.netnovel.comment.data.CommentType;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberCommentDto {
    //유저가 작성한 댓글,대댓글을 반환할때 사용하는 DTO

    //댓글, 대댓글의 Id
    private Long id;
    //댓글, 대댓글의 내용
    private String content;
    //타입, 댓글인지 대댓글인지 저장
    private CommentType type;

    //대댓글일 경우 상위 댓글의 id. 댓글이면 null 값
    private Long commentId;

    private String novelTitle;//소설제목

    private Long novelId;

    private String episodeTitle;//에피소드 제목

    private Long episodeId;//에피소드 아이디

    //생성시간
    private LocalDateTime createdAt;

    @Min(0)
    //좋아요 수
    private int likes;
    @Min(0)
    //싫어요 수
    private int disLikes;

    boolean isEditable;//수정/삭제 가능여부







}
