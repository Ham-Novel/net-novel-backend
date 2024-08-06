package com.ham.netnovel.comment;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
     *
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
     *
     * @param commentUpdateDto 클라이언트에서 보낸 내용을 담는 DTO,
     *                         content(댓글내용), episodeId, commentId ,providerId(유저정보)를 멤버변수로 가짐
     * @param bindingResult    DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication   유저의 인증 정보
     * @return ResponseEntity 처리 결과를 Httpstatus와 메시지에 담아 전송
     */
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable(name = "commentId") Long urlCommentId,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {

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

        //URL의 commentId와 RequestBody의 commentlId가 같은지 검증
        if (!urlCommentId.equals(commentUpdateDto.getCommentId())) {
            String errorMessage = "deleteComment API Error = 'Path Variable Id != Message Body Id'";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        commentService.updateComment(commentUpdateDto);

        return ResponseEntity.ok("댓글 수정 완료");

    }

    /**
     * 유저가 작성한 댓글의 상태를 DELETED_BY_USER로 변경하는 API
     *
     * @param commentDeleteDto 클라이언트에서 보낸 내용을 담는 DTO
     *                         episodeId, commentId ,providerId(유저정보)를 멤버변수로 가짐
     * @param bindingResult    DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication   유저의 인증 정보
     * @return ResponseEntity 처리 결과를 Httpstatus와 메시지에 담아 전송
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable(name = "commentId") Long urlCommentId,
            @Valid @RequestBody CommentDeleteDto commentDeleteDto,
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

        //URL의 commentId와 RequestBody의 commentlId가 같은지 검증
        if (!urlCommentId.equals(commentDeleteDto.getCommentId())) {
            String errorMessage = "deleteComment API Error = 'Path Variable Id != Message Body Id'";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        //댓글의 상태를 삭제 상태로 변경
        commentService.deleteComment(commentDeleteDto);

        log.info("댓글 삭제 요청 완료, commentId ={}", commentDeleteDto.getCommentId());
        return ResponseEntity.ok("삭제완료");

    }

    /**
     * 에피소드에 달린 댓글과 대댓글 정보를 전송하는 API
     * 댓글은 좋아요 순으로 정렬하여 전송
     * 댓글과 대댓글 DTO는 엔티티PK, content(내용), nickName(작성자닉네임), updatedAt(마지막으로 업데이트한 시각)을 멤버변수로 가짐
     * @return ResponseEntity 댓글 내용을 CommentListDto의 List 형태로 반환
     */
    @GetMapping("/episode/{episodeId}/comments")
    public ResponseEntity<?> getEpisodeComments(
            @PathVariable(name = "episodeId") Long episodeId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        if (sortBy.equals("recent")) {
            //특정 episode에 달린 댓글 정보를 List에 담음. 최신순으로 정렬
            return ResponseEntity.ok(commentService.getEpisodeCommentListByRecent(episodeId,pageable));
        }
        else if (sortBy.equals("likes")) {
            //특정 episode에 달린 댓글 정보를 List에 담음. 좋아요 순으로 정렬
            return ResponseEntity.ok(commentService.getEpisodeCommentListByLikes(episodeId,pageable));
        }
        else {
            //정렬 값이 없으면 예외 발생
            throw new IllegalArgumentException("postNovelComments: invalid sortBy option");
        }
    }

    /**
     * Novel(소설) Episode 에 달린 댓글과 대댓글 정보를 전송하는 API
     * 댓글은 최신 순으로 정렬
     * @return CommentEpisodeListDto 댓글과 대댓글 정보를 담는 객체
     */
    @GetMapping("/novel/{novelId}/comments")
    public ResponseEntity<List<CommentEpisodeListDto>> getNovelComments(
            @PathVariable(name = "novelId") Long novelId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        if (sortBy.equals("recent")) {
            //특정 Novel의 모든 Episode에 달린 댓글,대댓글 정보를 List에 담음, 댓글은 최신순으로 정렬
            return ResponseEntity.ok(commentService.getNovelCommentListByRecent(novelId,pageable));
        }
        else if (sortBy.equals("likes")) {
            //특정 Novel의 모든 Episode에 달린 댓글,대댓글 정보를 List에 담음, 댓글은 좋아요순으로 정렬
            return ResponseEntity.ok(commentService.getNovelCommentListByLikes(novelId,pageable));
        }
        else {
            //정렬 값이 없으면 예외 발생
            throw new IllegalArgumentException("getNovelComments: invalid sortBy option");
        }
    }


    //댓글 생성 테스트용 API
    @GetMapping("/test")
    public String commentTest() {


        return "/comment/comment-test";


    }


}
