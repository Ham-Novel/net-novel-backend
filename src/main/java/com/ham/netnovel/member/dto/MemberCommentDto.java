package com.ham.netnovel.member.dto;


import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberCommentDto {
    //유저가 작성한 댓글,대댓글을 반환할때 사용하는 DTO


    //타입, 댓글인지 대댓글인지 저장
    private CommentType type;

    //댓글, 대댓글의 Id
    private Long id;

    //댓글, 대댓글의 내용
    private String content;

    private LocalDateTime createAt;


    //최종 수정일
    private LocalDateTime updatedAt;




}
