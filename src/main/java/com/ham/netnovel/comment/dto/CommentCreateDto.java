package com.ham.netnovel.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString
@Builder
public class CommentCreateDto {


    //댓글 내용
    @NotBlank
    @Size(max = 300,message = "댓글은 300자 이하로 작성해주세요!")
    private String content;

    //에피소드의 Id값
    @NotNull
    private Long episodeId;

    private String providerId;




}
