package com.ham.netnovel.comment;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
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
     * @param commentCreateDto 클라이언트에서 보낸 내용을 담는 DTO
     *                         content(댓글내용), episodeId, providerId(유저정보)를 멤버변수로 가짐
     * @param authentication   유저의 인증정보
     * @return ResponseEntity 처리 결과를 Httpstatus와 메시지에 담아 전송
     */
    @PostMapping("/comments")
    public ResponseEntity<String> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
                                                BindingResult bindingResult,
                                                Authentication authentication) {

        //CommentCreateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("createComment API 에러발생 ={}", bindingResult.getFieldError());
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        commentCreateDto.setProviderId(principal.getName());

        //DTO 서비스 계층으로 넘겨 댓글 DB에 저장, 에러 발생시 예외로 던져짐
        commentService.createComment(commentCreateDto);

        return ResponseEntity.ok("댓글 추가 완료");
    }


    /**
     * 유저가 작성한 댓글의 내용을 업데이트하는 API
     * @param commentUpdateDto 클라이언트에서 보낸 내용을 담는 DTO,
     *                         content(댓글내용), episodeId, commentId ,providerId(유저정보)를 멤버변수로 가짐
     * @param bindingResult DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 처리 결과를 Httpstatus와 메시지에 담아 전송
     */
    @PatchMapping("/comments")
    public ResponseEntity<String> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto,
                                                BindingResult bindingResult,
                                                Authentication authentication) {

        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("updateComment API 에러발생 ={}", bindingResult.getFieldError());
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보(providerId) 값 저장
        commentUpdateDto.setProviderId(principal.getName());

        //
        commentService.updateComment(commentUpdateDto);

        return ResponseEntity.ok("댓글 수정 완료");

    }

    /**
     * 유저가 작성한 댓글의 상태를 DELETED_BY_USER로 변경하는 API
     * @param commentDeleteDto 클라이언트에서 보낸 내용을 담는 DTO
     *                         episodeId, commentId ,providerId(유저정보)를 멤버변수로 가짐
     * @param bindingResult DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 처리 결과를 Httpstatus와 메시지에 담아 전송
     */
    @DeleteMapping("/comments")
    public ResponseEntity<String> deleteComment(@Valid @RequestBody CommentDeleteDto commentDeleteDto,
                                                BindingResult bindingResult,
                                                Authentication authentication) {


        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("deleteComment API 에러발생 ={}", bindingResult.getFieldError());
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 정보 할당
        commentDeleteDto.setProviderId(principal.getName());

        //댓글의 상태를 삭제 상태로 변경
        commentService.deleteComment(commentDeleteDto);

        log.info("댓글 삭제 요청 완료, commentId ={}", commentDeleteDto.getCommentId());
        return ResponseEntity.ok("삭제완료");

    }


    /**
     * 에피소드에 달린 댓글과 대댓글 정보를 전송하는 API
     * 댓글과 대댓글 DTO는 엔티티PK, content(내용), nickName(작성자닉네임), updatedAt(마지막으로 업데이트한 시각)을 멤버변수로 가짐
     * @param requestBody episodeId값을 저장할 객체
     * @return ResponseEntity 댓글 내용을 CommentListDto의 List 형태로 반환
     */
    @PostMapping("/comments/episode")
    public ResponseEntity<?> getEpisodeCommentList(@RequestBody Map<String, String> requestBody) {
        String episodeId = requestBody.get("episodeId");

        try {
            //Long 타입으로 타입 캐스팅
            Long episodeIdLong = Long.valueOf(episodeId);

            List<CommentEpisodeListDto> commentList = commentService.getEpisodeCommentList(episodeIdLong);

            return ResponseEntity.ok(commentList);

        } catch (NumberFormatException e) {
            log.error("Invalid episodeId id: {}", episodeId, e);
            return ResponseEntity.badRequest().body("Invalid comment id: " + episodeId);
        }

    }

    /**
     * Novel(소설) 에피소드에 달린 댓글과 대댓글 정보를 전송하는 API
     * @param requestBody episodeId를 담는 객체
     * @return CommentEpisodeListDto 댓글과 대댓글 정보를 담는 객체
     */
    @PostMapping("/comments/novel")
    public ResponseEntity<?> postNovelCommentList(@RequestBody Map<String, String> requestBody){
        //클라이언트에서 받는 값 객체에 저장, String 타입임
        String novelIdStr = requestBody.get("novelId");
        //novelId를 정수타입으로 바꿀때 사용할 변수
        long novelIdLong;
        try {
            //Long 타입으로 타입 캐스팅
            novelIdLong = Long.parseLong(novelIdStr);
        }
        catch (Exception ex) {
            //예외 발생시 IllegalArgumentException로 던짐
                throw new IllegalArgumentException("postNovelCommentList API 에러, novelId가 정수가 아닙니다, novelId 값 ="+novelIdStr);
            }
            //Novel의 Episode에 달린 댓글과 대댓글을 DTO List로 받음
            List<CommentEpisodeListDto> novelCommentList = commentService.getNovelCommentList(novelIdLong);

            //클라이언트에 댓글,대댓글 정보 전송
            return ResponseEntity.ok(novelCommentList);

        }





    //댓글 생성 테스트용 API
    @GetMapping("/comment/test")
    public String commentTest() {


        return "/comment/comment-test";


    }


}
