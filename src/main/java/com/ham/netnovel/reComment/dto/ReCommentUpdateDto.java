package com.ham.netnovel.reComment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Builder
@Setter
public class ReCommentUpdateDto {

    @NotBlank(message = "대댓글을 입력해 주세요!")
    @Size(max = 300,message = "대댓글은 300자 이하로 작성해주세요!")
    private String content;


    //댓글의 Id값
    @NotNull
    private Long commentId;

    //대댓글 id값
    @NotNull
    private Long reCommentId;


    private String providerId;

}
