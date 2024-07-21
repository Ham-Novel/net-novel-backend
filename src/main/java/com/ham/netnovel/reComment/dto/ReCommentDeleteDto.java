package com.ham.netnovel.reComment.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReCommentDeleteDto {



    @NotNull
    private Long commentId;

    @NotNull
    private Long reCommentId;


    private String providerId;
}
