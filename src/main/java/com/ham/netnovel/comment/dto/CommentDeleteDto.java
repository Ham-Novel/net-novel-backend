package com.ham.netnovel.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentDeleteDto {


    //에피소드의 Id값
    @NotNull
    private Long episodeId;

    @NotNull
    private Long commentId;


    private String providerId;

}
