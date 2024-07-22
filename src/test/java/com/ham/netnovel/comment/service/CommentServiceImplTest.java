package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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


    /*
    테스트 성공
     */
    @Test
    public void getCommentListTest(){
        System.out.println("댓글 불러오기 테스트");

        List<CommentListDto> commentList = commentService.getCommentList(2306L);
//        List<CommentListDto> commentList = commentService.getCommentList(909090909L);//존재하지 않는 에피소드 테스트, 빈리스트 반환됨

        for (CommentListDto commentListDto : commentList) {
            System.out.println("댓글정보");

            List<ReCommentListDto> reCommentList = commentListDto.getReCommentList();
            System.out.println(commentListDto.toString());


            //대댓글 정보 출력
            System.out.println("대댓글 정보");
            for (ReCommentListDto reCommentListDto : reCommentList) {

                System.out.println(reCommentListDto.toString());

            }



        }


    }

}