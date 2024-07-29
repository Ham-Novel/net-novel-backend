package com.ham.netnovel.commentLike.service;

import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentLikeServiceImplTest {


    private final CommentLikeService commentLikeService;

    @Autowired
    CommentLikeServiceImplTest(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }


    @Test
    void toggleCommentLikeStatus() {

        String providerId = "test";

//        존재하지 않는 사용자 테스트 NoSuchElementException 던져짐
//        String providerId = "ccc";



//        Long commentId = 14L;
        //존재하지 않는 댓글 테스트 NoSuchElementException 던져짐
        Long commentId = 56445646L;


        LikeType like = LikeType.LIKE;

        CommentLikeToggleDto build = CommentLikeToggleDto.builder()
                .providerId(providerId)
                .likeType(like)
                .commentId(commentId)
                .build();


        boolean result = commentLikeService.toggleCommentLikeStatus(build);

        System.out.println(result);

    }
}