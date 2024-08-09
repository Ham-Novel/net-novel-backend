package com.ham.netnovel.reComment;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.reComment.dto.ReCommentCreateDto;
import com.ham.netnovel.reComment.dto.ReCommentDeleteDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import com.ham.netnovel.reComment.dto.ReCommentUpdateDto;
import com.ham.netnovel.reComment.service.ReCommentService;
import com.ham.netnovel.common.utils.Authenticator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("/api")
@Slf4j
public class ReCommentController {

    private final ReCommentService reCommentService;

    private final Authenticator authenticator;


    @Autowired
    public ReCommentController(ReCommentService reCommentService, Authenticator authenticator) {
        this.reCommentService = reCommentService;
        this.authenticator = authenticator;
    }

    @PostMapping("/recomment")
    public ResponseEntity<String> createReComment(@Valid @RequestBody ReCommentCreateDto reCommentCreateDto,
                                                  BindingResult bindingResult,
                                                  Authentication authentication) {

        //ReCommentCreateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("createComment API 에러발생 ={}", bindingResult.getFieldError());
            return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        reCommentCreateDto.setProviderId(principal.getName());

        reCommentService.createReComment(reCommentCreateDto);

        return ResponseEntity.ok("댓글 추가 완료");

    }


    @PatchMapping("/recomment")
    public ResponseEntity<String> updateReComment(@Valid @RequestBody ReCommentUpdateDto reCommentUpdateDto,
                                                  BindingResult bindingResult,
                                                  Authentication authentication) {

        //ReCommentCreateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("createComment API 에러발생 ={}", bindingResult.getFieldError());
            return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        reCommentUpdateDto.setProviderId(principal.getName());

        reCommentService.updateReComment(reCommentUpdateDto);

        return ResponseEntity.ok("대댓글 수정 완료");


    }

    @DeleteMapping("/recomment")
    public ResponseEntity<String> deleteReComment(@Valid @RequestBody ReCommentDeleteDto reCommentDeleteDto,
                                                  BindingResult bindingResult,
                                                  Authentication authentication) {

        //ReCommentDeleteDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()){
            log.error("deleteReComment API 에러발생 ={}", bindingResult.getFieldError());
            return ResponseEntity.badRequest().body("에러발생");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        reCommentDeleteDto.setProviderId(principal.getName());

        reCommentService.deleteReComment(reCommentDeleteDto);

        log.info("대댓글 삭제 요청 완료, commentId ={}",reCommentDeleteDto.getCommentId());
        return ResponseEntity.ok("삭제완료");


    }


    @PostMapping("/recomment/list")
    public ResponseEntity<?> getReCommentList(@RequestBody Map<String, String> requestBody) {

        String commentId = requestBody.get("commentId");
        try {
            //Long 타입으로 타입 캐스팅
            Long commentIdLong = Long.valueOf(commentId);

            List<ReCommentListDto> reCommentList = reCommentService.getReCommentList(commentIdLong);
            return ResponseEntity.ok(reCommentList);
        } catch (NumberFormatException e) {
            log.error("Invalid comment id: {}", commentId, e);
            return ResponseEntity.badRequest().body("Invalid comment id: " + commentId);
        }
    }


    //댓글 생성 테스트용 API
    @GetMapping("/recomment/test")
    public String commentTest() {

        return "/reComment/re-comment-test";


    }


}
