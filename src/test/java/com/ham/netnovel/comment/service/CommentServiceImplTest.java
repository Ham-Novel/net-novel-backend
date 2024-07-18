package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.dto.CommentCreateDto;
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


}