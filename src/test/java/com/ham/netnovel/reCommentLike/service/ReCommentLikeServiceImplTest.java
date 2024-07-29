package com.ham.netnovel.reCommentLike.service;

import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.reCommentLike.ReCommentLikeToggleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ReCommentLikeServiceImplTest {

    private final ReCommentLikeService reCommentLikeService;

    @Autowired
    ReCommentLikeServiceImplTest(ReCommentLikeService reCommentLikeService) {
        this.reCommentLikeService = reCommentLikeService;
    }

    //테스트성공
    @Test
    void toggleReCommentLikeStatus() {
        String providerId = "test";

//        존재하지 않는 사용자 테스트 NoSuchElementException 던져짐
//        String providerId = "ccc";



        Long reCommentId = 151L;
        //존재하지 않는 댓글 테스트 NoSuchElementException 던져짐
//        Long reCommentId = 56445646L;


        LikeType like = LikeType.LIKE;

        ReCommentLikeToggleDto build = ReCommentLikeToggleDto.builder()
                .providerId(providerId)
                .likeType(like)
                .reCommentId(reCommentId)
                .build();

        boolean result = reCommentLikeService.toggleReCommentLikeStatus(build);

        System.out.println(result);

    }
}