package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CommentServiceImplTest {

    private final CommentService commentService;


    @Autowired
    CommentServiceImplTest(CommentService commentService) {
        this.commentService = commentService;
    }


    //2024-07-18 테스트 성공
    @Test
    public void createCommentTest() {
        System.out.println("테스트");

        CommentCreateDto build = CommentCreateDto.builder()
                .episodeId(2306L)//테스트용 episdoe
                .content("테스트 댓글")
                .providerId("UEqG1Al3FwPQTqDy6tfFb2MGZyEUd-weiJUzyxnkJhM")//테스트용 유저
                .build();

        commentService.createComment(build);


    }

    /*
    테스트 완료
    테스트 항목
    1. 정상적인 댓글수정 요청시, 댓글 내용 수정 후 updated_at 수정
     */
    @Test
    public void updateCommentTest(){

        System.out.println("테스트");
        CommentUpdateDto build = CommentUpdateDto.builder()
                .content("수정 테스트")
                .episodeId(2306L)//테스트용 episdoe
                .providerId("UEqG1Al3FwPQTqDy6tfFb2MGZyEUd-weiJUzyxnkJhM")//테스트용 유저
                .commentId(16L)
                .build();
        commentService.updateComment(build);
    }


    /*
    테스트 완료
     */
    @Test
    public void deleteCommentTest(){
        System.out.println("삭제 상태 변경 테스트");
        CommentDeleteDto build = CommentDeleteDto.builder()
                .episodeId(2306L)//테스트용 episdoe
                .providerId("UEqG1Al3FwPQTqDy6tfFb2MGZyEUd-weiJUzyxnkJhM")//테스트용 유저
                .commentId(16L)
                .build();
        commentService.deleteComment(build);


    }

}