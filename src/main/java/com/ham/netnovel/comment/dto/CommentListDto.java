package com.ham.netnovel.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListDto {


    private Long commentId;

    private String content;

    //작성자 닉네임
    private String nickName;

    private LocalDateTime updatedAt;


}
