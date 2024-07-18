package com.ham.netnovel.comment;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.service.CommentService;
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


    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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

        //유저 정보가 없으면 badRequest 응답
        if (!authentication.isAuthenticated()){
            return ResponseEntity.badRequest().body("로그인 정보가 없습니다.");
        }

        //principal 타입캐스팅
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        //DTO에 유저 정보(providerId) 값 저장
        commentCreateDto.setProviderId(principal.getName());

        //DTO 서비스 계층으로 넘겨 댓글 DB에 저장, 에러 발생시 예외로 던져짐
        commentService.createComment(commentCreateDto);

        return ResponseEntity.ok("댓글 추가 완료");

    }



    //댓글 생성 테스트용 API
    @GetMapping("/episode/{id}")
    public String commentTest(@PathVariable Integer id){


        return "/comment/comment-test";


    }




}
