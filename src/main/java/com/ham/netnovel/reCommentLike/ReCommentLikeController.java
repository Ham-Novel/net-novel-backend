package com.ham.netnovel.reCommentLike;

import com.ham.netnovel.commentLike.data.LikeResult;
import com.ham.netnovel.commentLike.data.LikeType;
import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import com.ham.netnovel.reCommentLike.service.ReCommentLikeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/re-comment-likes")
public class ReCommentLikeController {

    private final ReCommentLikeService reCommentLikeService;

    private final Authenticator authenticator;

    public ReCommentLikeController(ReCommentLikeService reCommentLikeService, Authenticator authenticator) {
        this.reCommentLikeService = reCommentLikeService;
        this.authenticator = authenticator;
    }

    /**
     * 대댓글에 대한 좋아요 또는 싫어요 요청을 처리하는 API입니다.
     *
     * <p>사용자가 대댓글에 좋아요를 눌렀는지의 여부를 확인하고,
     * 기록이 없으면 이를 DB에 저장하며, 이미 기록이 존재하면 해당 기록을 삭제합니다.</p>
     *
     * <p>만약 유저가, 감정표현을 한 상태에서, 다른 감정표현을 클릭시(기존에 좋아요 누른 상태에서 싫어요 선택)
     * HTTP 400 코드와 에러메시지를 전달합니다.</p>
     *
     * @param reCommentLikeToggleDto reCmmentId, providerId(유저 정보), likeType(좋아요,싫어요 정보) 담
     *                             는 {@link ReCommentLikeToggleDto} 객체
     * @param bindingResult        DTO 유효성 검사 결과
     * @param authentication       유저 인증 정보
     * @return {@link ResponseEntity} 감정 등록시 true, 삭제시 false, 실패시 HTTP 400 코드 전송
     */
    @PostMapping
    public ResponseEntity<?> toggleReCommentLikeStatus(
            @Valid @RequestBody ReCommentLikeToggleDto reCommentLikeToggleDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "toggleReCommentLikeStatus");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //DTO에 유저 인증 정보 저장
        reCommentLikeToggleDto.setProviderId(principal.getName());

        //유저가 대댓글에 좋아요 누른 기록이 있으면 삭제후 false 반환, 없으면 DB에 저장후 true 반환
        LikeResult likeResult = reCommentLikeService.toggleReCommentLikeStatus(reCommentLikeToggleDto);

        // LikeResult에 따른 결과 처리
        switch (likeResult) {
            case CREATION -> {
                return ResponseEntity.ok(true);
            }//대댓글 감정표현 저장 완료
            case DELETION -> {
                return ResponseEntity.ok(false);
            }//댓글 감정표현 삭제 완료
            case FAILURE -> {
                //false 일경우 badRequest 전송
                //클라이언트가 현재 선택한 타입과 반대타입 할당(기존에 선택한 감정표현을 에러메시지로 전송하기 위함)
                String typeName = "싫어요";
                //싫어요일경우 재할당
                if (reCommentLikeToggleDto.getLikeType().equals(LikeType.DISLIKE)) {
                    typeName = "좋아요";
                }
                String massage = "이미 " + typeName + "선택한 대댓글입니다. 취소후 수정해주세요!";
                return ResponseEntity.badRequest().body(massage);
            }
            default -> {
                return ResponseEntity.internalServerError().body("알수없는에러발생");
            }
        }

    }
}
