package com.ham.netnovel.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class CommentUpdateDto {
    //댓글 내용
    @NotBlank
    @Size(max = 300,message = "댓글은 300자 이하로 작성해주세요!")
    private String content;


    //에피소드의 Id값
    @NotNull
    private Long episodeId;

    @NotNull
    private Long commentId;



    private String providerId;


}
