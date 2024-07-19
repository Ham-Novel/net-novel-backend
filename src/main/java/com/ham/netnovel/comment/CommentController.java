package com.ham.netnovel.comment;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.utils.Authenticator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class CommentController {


    private final CommentService commentService;

    private final Authenticator authenticator;


    @Autowired
    public CommentController(CommentService commentService, Authenticator authenticator) {
        this.commentService = commentService;
        this.authenticator = authenticator;
    }


    /**
     * 유저가 작성한 댓글(comment)를 서버에 저장하는 API
     * @param commentCreateDto 댓글내용, 에피소드id를 저장하는 DTO
     * @param authentication 유저의 인증정보
     * @return ResponseEntity 요청 실패시 badRequest 전송
     */
    @PostMapping("/comment")
    public ResponseEntity<String> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
                                                BindingResult bindingResult,
                                                Authentication authentication){

        //CommentCreateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()){
            log.error("createComment API 에러발생 ={}",String.valueOf(bindingResult.getFieldError()));
             return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        commentCreateDto.setProviderId(principal.getName());

        //DTO 서비스 계층으로 넘겨 댓글 DB에 저장, 에러 발생시 예외로 던져짐
        commentService.createComment(commentCreateDto);

        return ResponseEntity.ok("댓글 추가 완료");
    }


    //댓글 수정 기능
    @PatchMapping("/comment")
    public ResponseEntity<String> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto,
                                                BindingResult bindingResult,
                                                Authentication authentication){

        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()){
            log.error("updateComment API 에러발생 ={}",String.valueOf(bindingResult.getFieldError()));
            return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        commentUpdateDto.setProviderId(principal.getName());

        commentService.updateComment(commentUpdateDto);

        return ResponseEntity.ok("댓글 수정 완료");

    }

    @DeleteMapping("/comment")
    public ResponseEntity<String> deleteComment(@Valid @RequestBody CommentDeleteDto commentDeleteDto,
                                                BindingResult bindingResult,
                                                Authentication authentication) {


        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()){
            log.error("updateComment API 에러발생 ={}",String.valueOf(bindingResult.getFieldError()));
            return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보 할당
        commentDeleteDto.setProviderId(principal.getName());

        //댓글의 상태를 삭제 상태로 변경
        commentService.deleteComment(commentDeleteDto);

        log.info("댓글 삭제 요청 완료, commentId ={}",commentDeleteDto.getCommentId());
        return ResponseEntity.ok("삭제완료");

    }

    /**
     * Episode에 등록된 댓글을 반환하는 API
     * @param commentDeleteDto
     * @param bindingResult Bean Validation 에러 정보
     * @param authentication 유저 인증정보
     * @return
     */
    @GetMapping("/comment")
    public ResponseEntity<String> getCommentList(@Valid @RequestBody CommentDeleteDto commentDeleteDto,
                                                BindingResult bindingResult,
                                                Authentication authentication) {


        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);


        //episode와 연관된
        //댓글 전부 가져와서
        //반환해주세요~
        return ResponseEntity.ok("ok");
    }




        //댓글 생성 테스트용 API
    @GetMapping("/episode/{id}")
    public String commentTest(@PathVariable Integer id){


        return "/comment/comment-test";


    }




}
