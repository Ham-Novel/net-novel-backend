package com.ham.netnovel.reComment.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ReCommentDeleteDto {



    private Long commentId;

    private Long reCommentId;


    private String providerId;
}
