package com.ham.netnovel.commentLike;

import com.ham.netnovel.commentLike.data.LikeResult;
import com.ham.netnovel.commentLike.data.LikeType;
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
     * 댓글에 대한 좋아요 또는 싫어요 요청을 처리하는 API입니다.
     *
     * <p>사용자가 댓글에 좋아요를 눌렀는지의 여부를 확인하고,
     * 기록이 없으면 이를 DB에 저장하며, 이미 기록이 존재하면 해당 기록을 삭제합니다.</p>
     *
     * <p>만약 유저가, 감정표현을 한 상태에서, 다른 감정표현을 클릭시(기존에 좋아요 누른 상태에서 싫어요 선택)
     * HTTP 400 코드와 에러메시지를 전달합니다.</p>
     *
     * @param commentLikeToggleDto commentId, providerId(유저 정보), likeType(좋아요,싫어요 정보) 담
     *                             는 {@link CommentLikeToggleDto} 객체
     * @param bindingResult        DTO 유효성 검사 결과
     * @param authentication       유저 인증 정보
     * @return {@link ResponseEntity} 감정 등록시 true, 삭제시 false, 실패시 HTTP 400 코드 전송
     */
    @PostMapping("comments/{commentId}/comment-likes")
    public ResponseEntity<?> toggleCommentLikeStatus(
            @Valid @RequestBody CommentLikeToggleDto commentLikeToggleDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "createEpisode");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 인증 정보 저장
        commentLikeToggleDto.setProviderId(principal.getName());

        //댓글 감정표현 저장 결과 반환, 감정표현 생성시 CREATION , 삭제시 DELETION 실패시 FAILURE 반환
        LikeResult likeResult = commentLikeService.toggleCommentLikeStatus(commentLikeToggleDto);

        //클라이언트가 선택한 감정표현 타입
        String typeName = "좋아요";
        //싫어요일경우 재할당
        if (commentLikeToggleDto.getLikeType().equals(LikeType.DISLIKE)) {
            typeName = "싫어요";
        }
        // LikeResult에 따른 결과 처리
        switch (likeResult) {
            case CREATION -> {
                return ResponseEntity.ok(true);
            }//댓글 감정표현 저장 완료
            case DELETION -> {
                return ResponseEntity.ok(false);
            }//댓글 감정표현 삭제 완료
            case FAILURE -> {//false 일경우 badRequest 전송
                String massage = "이미 " + typeName + "선택한 댓글입니다. 취소후 수정해주세요!";
                return ResponseEntity.badRequest().body(massage);
            }
            default -> {
                return ResponseEntity.internalServerError().body("알수없는에러발생");
            }
        }


    }
}
