package com.ham.netnovel.comment;


import com.ham.netnovel.comment.data.CommentSortOrder;
import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Controller
@Slf4j
@RequestMapping("/api")
@Tag(name = "Comments", description = "댓글과 관련된 작업")
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
    @Operation(summary = "댓글 작성 API",
            description = "클라이언트에서 전달한 댓글 내용을 서버에 저장합니다.된 사용자만 접근할 수 있습니다. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 추가 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "댓글 추가 완료"))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Bad Request", "message": "댓글 내용은 비워둘 수 없습니다.", "status": 400 }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Unauthorized", "message": "로그인 정보가 없습니다.", "status": 401 }
                                    """)))
    })
    @PostMapping("/comments")
    public ResponseEntity<String> createComment(
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //클라이언트에서 보낸 데이터 유효성 검사, 에러가 있을경우 에러메시지 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult, "createComment");
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
    @Operation(
            summary = "댓글 수정 API",
            description = """
                        클라이언트에서 전달한 데이터를 기반으로 댓글 내용을 수정합니다.
                        인증된 사용자만 접근할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "댓글 수정 완료"))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Bad Request", "message": "댓글 내용은 비워둘 수 없습니다.", "status": 400 }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Unauthorized", "message": "로그인 정보가 없습니다.", "status": 401 }
                                    """)))
    })
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
    @Operation(
            summary = "댓글 삭제 API",
            description = """
                        클라이언트에서 전달한 데이터를 기반으로 댓글 상태를 삭제 상태로 변경합니다.
                        인증된 사용자만 접근할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "삭제완료"))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Bad Request", "message": "댓글 ID는 필수입니다.", "status": 400 }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Unauthorized", "message": "로그인 정보가 없습니다.", "status": 401 }
                                    """)))
    })
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

        //댓글의 상태를 삭제 상태로 변경
        commentService.deleteComment(commentDeleteDto);

        log.info("댓글 삭제 요청 완료, commentId ={}", commentDeleteDto.getCommentId());
        return ResponseEntity.ok("삭제완료");

    }

    /**
     * 에피소드에 달린 댓글과 대댓글 정보를 전송하는 API 입니다.
     * 댓글은 좋아요 또는 최신순으로 정렬하여 전송
     * 댓글과 대댓글 DTO는 id, content(내용), nickName(작성자닉네임), updatedAt(마지막으로 업데이트한 시각)을 멤버변수로 가짐
     *
     * @return ResponseEntity 댓글 내용을 {@link CommentEpisodeListDto} List 형태로 반환
     */

    @Operation(
            summary = "에피소드 댓글 조회 API",
            description = """
                        특정 에피소드에 달린 댓글과 대댓글을 조회합니다.
                        댓글은 최신순 또는 좋아요순으로 정렬할 수 있습니다.
                        인증되지 않은 사용자는 기본 조회만 가능합니다.
                    """,
            parameters = {
                    @Parameter(name = "episodeId",description = "에피소드의 ID",example = "123"),
                    @Parameter(name = "sortBy",description = "에피소드의 ID",example = "123"),
                    @Parameter(name = "pageNumber", description = "페이지번호 0부터 시작", example = "0"),
                    @Parameter(name = "pageSize", description = "페이지 크기 기본값 10", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentEpisodeListDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Bad Request", "message": "정렬 기준은 'recent' 또는 'likes' 중 하나여야 합니다.", "status": 400 }
                                    """)))
    })


    @GetMapping("/episodes/{episodeId}/comments")
    public ResponseEntity<?> getEpisodeComments(
            @PathVariable(name = "episodeId") Long episodeId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            Authentication authentication) {

        //유저 인증정보가 있을경우 검증후 객체에 할당, 없으면 "NON_LOGIN" 할당
        String providerId;
        if (authentication == null) {
            providerId = "NON_LOGIN";
        } else {
            CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
            providerId = principal.getName();
        }
        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        //유저가 선택한 정렬방식이 최신순 일경우, 최신순으로 댓글을 정렬하여 반환
        if (sortBy.equals("recent")) {
            return ResponseEntity.ok(commentService.getEpisodeComment(episodeId, pageable, providerId, CommentSortOrder.RECENT));
        }
        //정렬 기본값은 좋아요순, 좋아요순으로 댓글을 정렬하여 반환
        return ResponseEntity.ok(commentService.getEpisodeComment(episodeId, pageable, providerId, CommentSortOrder.LIKES));

    }

    //ToDo 유저검증로직 추가(수정삭제용)

    /**
     * Novel(소설) Episode 에 달린 댓글과 대댓글 정보를 전송하는 API
     * 댓글은 최신 순으로 정렬
     *
     * @return CommentEpisodeListDto 댓글과 대댓글 정보를 담는 객체
     */

    @Operation(
            summary = "소설 댓글 조회 API",
            description = """
                        특정 소설의 모든 에피소드에 달린 댓글과 대댓글을 조회합니다.
                        댓글은 최신순 또는 좋아요순으로 정렬할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentEpisodeListDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Bad Request", "message": "정렬 기준은 'recent' 또는 'likes' 중 하나여야 합니다.", "status": 400 }
                                    """)))
    })
    @GetMapping("/novels/{novelId}/comments")
    public ResponseEntity<List<CommentEpisodeListDto>> getNovelComments(
            @PathVariable(name = "novelId") Long novelId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        if (sortBy.equals("recent")) {
            //특정 Novel의 모든 Episode에 달린 댓글,대댓글 정보를 List에 담음, 댓글은 최신순으로 정렬
            return ResponseEntity.ok(commentService.getNovelCommentListByRecent(novelId, pageable));
        } else if (sortBy.equals("likes")) {
            //특정 Novel의 모든 Episode에 달린 댓글,대댓글 정보를 List에 담음, 댓글은 좋아요순으로 정렬
            return ResponseEntity.ok(commentService.getNovelCommentListByLikes(novelId, pageable));
        } else {
            //정렬 값이 없으면 예외 발생
            throw new IllegalArgumentException("getNovelComments: invalid sortBy option");
        }
    }


}
