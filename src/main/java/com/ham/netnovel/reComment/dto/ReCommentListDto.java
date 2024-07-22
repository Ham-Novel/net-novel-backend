package com.ham.netnovel.reComment.dto;

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

    private LocalDateTime updatedAt;



}
