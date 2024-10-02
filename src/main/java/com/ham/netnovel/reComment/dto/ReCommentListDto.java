package com.ham.netnovel.reComment.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ReCommentListDto {

    private Long reCommentId;

    private String content;

    //작성자 닉네임
    private String nickName;


    @Min(0)
    //좋아요 수
    private int likes;
    @Min(0)
    //싫어요 수
    private int disLikes;

    boolean isEditable;//수정/삭제 가능여부

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



}
