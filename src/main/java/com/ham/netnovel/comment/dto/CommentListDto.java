package com.ham.netnovel.comment.dto;

import com.ham.netnovel.reComment.dto.ReCommentListDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentListDto {//뷰에 반환할때 사용하는 DTO


    private Long commentId;

    private String content;

    //작성자 닉네임
    private String nickName;

    private LocalDateTime updatedAt;

    private List<ReCommentListDto> reCommentList;

}

