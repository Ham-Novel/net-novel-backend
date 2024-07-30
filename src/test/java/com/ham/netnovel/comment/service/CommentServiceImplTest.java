package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


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
                .providerId("")//테스트용 유저
                .build();

        commentService.createComment(build);


    }

    /*
    테스트 완료
    테스트 항목
    1. 정상적인 댓글수정 요청시, 댓글 내용 수정 후 updated_at 수정
     */
    @Test
    public void updateCommentTest() {

        System.out.println("테스트");
        CommentUpdateDto build = CommentUpdateDto.builder()
                .content("수정 테스트")
                .episodeId(2306L)//테스트용 episdoe
                .providerId("")//테스트용 유저
                .commentId(16L)
                .build();
        commentService.updateComment(build);
    }


    /*
    테스트 완료
     */
    @Test
    public void deleteCommentTest() {
        System.out.println("삭제 상태 변경 테스트");
        CommentDeleteDto build = CommentDeleteDto.builder()
                .episodeId(2306L)//테스트용 episdoe
                .providerId("")//테스트용 유저
                .commentId(16L)
                .build();
        commentService.deleteComment(build);


    }


    //테스트 성공, 최신순으로 정렬됨
    @Test
    public void getEpisodeCommentListByRecent() {
        System.out.println("댓글 불러오기 테스트");

        List<CommentEpisodeListDto> commentList = commentService.getEpisodeCommentListByRecent(2310L);
//        List<CommentEpisodeListDto> commentList = commentService.getCommentList(909090909L);//존재하지 않는 에피소드 테스트, 빈리스트 반환됨

        printCommentAndReCommentInfo(commentList);
    }



    //테스트 성공 ,좋아요 순서로 정렬
    @Test
    void getEpisodeCommentListByLikes() {
        Long episodeId = 2310L;
        List<CommentEpisodeListDto> episodeCommentListByLikes = commentService.getEpisodeCommentListByLikes(episodeId);

        //출력 테스트
        printCommentAndReCommentInfo(episodeCommentListByLikes);

    }

    //    테스트 완료
    // 유저가 작성한 댓글이 없으면 빈 리스트 반환
    // createAt를 기준으로 정렬됨
    @Test
    public void getMemberCommentList() {


        String providerId = "";
        //댓글을 작성한적이 없는 유저 테스트
//        String providerId= "ttt";

        List<MemberCommentDto> memberCommentList = commentService.getMemberCommentList(providerId);

        if (memberCommentList.isEmpty()) {
            System.out.println("비었음");
        }

        for (MemberCommentDto memberCommentDto : memberCommentList) {
            System.out.println("댓글");
            System.out.println(memberCommentDto.toString());


        }


    }

    //테스트 성공
    @Test
    public void getNovelCommentListRecent() {
        Long novelId = 1L;
//        Long novelId =7L;
        List<CommentEpisodeListDto> novelCommentList = commentService.getNovelCommentListByRecent(novelId);
        //출력 테스트
        printCommentAndReCommentInfo(novelCommentList);
    }

    //테스트 성공
    //에피소드 상관 없이, 좋아요 순서대로 댓글 정렬됨
    @Test
    void getNovelCommentListByLikes() {
        Long novelId = 1L;
//        Long novelId =7L;
        List<CommentEpisodeListDto> novelCommentListByLikes = commentService.getNovelCommentListByLikes(novelId);

        printCommentAndReCommentInfo(novelCommentListByLikes);

    }

    //댓글,대댓글 정보 출력 테스트용 메서드
    private void printCommentAndReCommentInfo(List<CommentEpisodeListDto> list) {

        for (CommentEpisodeListDto dto : list) {
            System.out.println("*******댓글 정보********");
            System.out.println("에피소드 제목= " + dto.getEpisodeTitle());
            System.out.println("작성자 닉네임= " + dto.getNickName());
            System.out.println("댓글내용 = " + dto.getContent());
            System.out.println("댓글 작성시간= " + dto.getCreatedAt());
            System.out.println("좋아요 수 =" + dto.getLikes());
            System.out.println("싫어요 수 =" + dto.getDisLikes());

            List<ReCommentListDto> reCommentList = dto.getReCommentList();
            for (ReCommentListDto reCommentListDto : reCommentList) {
                System.out.println("******대댓글 정보*******");
                System.out.println("작성자 닉네임=" + reCommentListDto.getNickName());
                System.out.println("댓글내용 = " + reCommentListDto.getContent());
                System.out.println("댓글 작성시간= " + reCommentListDto.getCreatedAt());
                System.out.println("좋아요 수 =" + reCommentListDto.getLikes());
                System.out.println("싫어요 수 =" + reCommentListDto.getDisLikes());
            }

        }


    }


}