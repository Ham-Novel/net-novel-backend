package com.ham.netnovel.reComment;


import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
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

@Controller
@Slf4j
@RequestMapping("/api")
public class ReCommentController {

    private final ReCommentService reCommentService;

    private final Authenticator authenticator;


    @Autowired
    public ReCommentController(ReCommentService reCommentService, Authenticator authenticator) {
        this.reCommentService = reCommentService;
        this.authenticator = authenticator;
    }

    @PostMapping("/recomment")
    public ResponseEntity<String> createReComment(
            @Valid @RequestBody ReCommentCreateDto reCommentCreateDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "createReComment");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        reCommentCreateDto.setProviderId(principal.getName());

        reCommentService.createReComment(reCommentCreateDto);

        return ResponseEntity.ok("댓글 추가 완료");

    }

    /**
     * 대댓글을 수정하는 API 입니다.
     *
     * <p>클라이언트에서 전송한 데이터를 검증한 후, 이상이 있으면 BadRequest를 전송합니다.
     * 이상이 없으면 대댓글을 수정후  응답코드를 전송합니다.  </p>
     *
     * @param reCommentUpdateDto 수정할 대댓글의 데이터를 담고 있는 {@link ReCommentUpdateDto} 객체
     * @param bindingResult      DTO의 유효성 검사 결과가 담긴 객체. 에러가 있을 경우 HTTP 400 코드와 에러메시지 전송
     * @param authentication     현재 사용자 인증 정보를 담고 있는 객체. 인증된 사용자 정보가 없으면 HTTP 401 전송
     * @return {@link ResponseEntity<String>} 대댓글 업데이트 결과를 담고 있는 응답 객체 반환
     */
    @PatchMapping("/recomment")
    public ResponseEntity<String> updateReComment(
            @Valid @RequestBody ReCommentUpdateDto reCommentUpdateDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "updateReComment");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보 할당
        reCommentUpdateDto.setProviderId(principal.getName());
        //대댓글 업데이트
        reCommentService.updateReComment(reCommentUpdateDto);
        return ResponseEntity.ok("대댓글 수정 완료");


    }

    /**
     * 대댓글을 삭제상태로 변경하는 API 입니다.
     *
     * <p>클라이언트에서 전송한 데이터를 검증한 후, 이상이 있으면 BadRequest를 전송합니다.
     * 이상이 없으면 대댓글을 삭제 상태로 변경하고, 응답코드를 전송합니다.  </p>
     *
     * @param reCommentDeleteDto 삭제할 대댓글의 데이터를 담고 있는 {@link ReCommentDeleteDto} 객체
     * @param bindingResult      DTO의 유효성 검사 결과가 담긴 객체. 에러가 있을 경우 HTTP 400 코드와 에러메시지 전송
     * @param authentication     현재 사용자 인증 정보를 담고 있는 객체. 인증된 사용자 정보가 없으면 HTTP 401 전송
     * @return {@link ResponseEntity<String>} 대댓글 삭제 결과를 담고 있는 응답 객체 반환
     */
    @DeleteMapping("/recomment")
    public ResponseEntity<String> deleteReComment(
            @Valid @RequestBody ReCommentDeleteDto reCommentDeleteDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "updateReComment");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보 할당
        reCommentDeleteDto.setProviderId(principal.getName());
        //대댓글 삭제상태로 변경
        reCommentService.deleteReComment(reCommentDeleteDto);
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
