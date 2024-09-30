package com.ham.netnovel.commentLike;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.commentLike.service.CommentLikeService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    private final Authenticator authenticator;

    public CommentLikeController(CommentLikeService commentLikeService, Authenticator authenticator) {
        this.commentLikeService = commentLikeService;
        this.authenticator = authenticator;
    }

    /**
     * 댓글 좋아요 요청을 받아 처리하는 API
     * 댓글에 좋아요를 누른 기록이 없으면, 해당 내용 DB에 저장
     * 댓글에 좋아요를 누른 기록이 있으면 기록 삭제
     * @param commentLikeToggleDto commentId, providerId(유저 정보), likeType(좋아요,싫어요 정보) 담는 DTO
     * @param bindingResult DTO 유효성 검사 결과
     * @param authentication 유저 인증 정보
     * @return ResponseEntity body에 내용 담아 전달
     */
    @PostMapping("comments/{commentId}/comment-likes")
    public ResponseEntity<?> toggleCommentLikeStatus(
            @PathVariable(name = "commentId") Long urlCommentId,
            @Valid @RequestBody CommentLikeToggleDto commentLikeToggleDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()){
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //서버에 로그 출력
            log.error("toggleCommentLikeStatus API 에러발생 ={}",errorMessages);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 인증 정보 저장
        commentLikeToggleDto.setProviderId(principal.getName());

        //URL의 commentId와 RequestBody의 commentlId가 같은지 검증
        if (!urlCommentId.equals(commentLikeToggleDto.getCommentId())) {
            String errorMessage = "deleteComment API Error = 'Path Variable Id != Message Body Id'";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        //유저가 댓글에 좋아요 누른 기록이 있으면 삭제후 false 반환, 없으면 DB에 저장후 true 반환
        boolean result = commentLikeService.toggleCommentLikeStatus(commentLikeToggleDto);

        //결과 검증
        if(result){
            return ResponseEntity.ok("좋아요 등록 완료");
        }else {
            return ResponseEntity.ok("좋아요 삭제 완료");
        }


    }


}
