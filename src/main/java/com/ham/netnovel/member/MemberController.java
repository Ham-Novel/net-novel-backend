package com.ham.netnovel.member;


import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.member.dto.*;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/api")
@Tag(name = "Member", description = "유저와 관련된 작업")

public class MemberController {

    private final Authenticator authenticator;

    private final MemberService memberService;

    private final MemberMyPageService memberMyPageService;

    public MemberController(Authenticator authenticator, MemberService memberService, MemberMyPageService memberMyPageService) {
        this.authenticator = authenticator;
        this.memberService = memberService;
        this.memberMyPageService = memberMyPageService;
    }


    /**
     * 현재 인증된 사용자의 마이페이지 정보를 조회하여 반환하는 API 입니다.
     *
     * <p>사용자의 인증 정보를 확인하고, 유효하지 않은 경우 Bad Request 응답을 반환합니다.
     * 인증이 유효할 경우, 해당 사용자의 정보를 데이터베이스에서 조회하여 반환합니다.</p>
     *
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 사용자의 마이페이지 정보가 포함된 {@link ResponseEntity} 객체
     * @throws IllegalArgumentException 유저 인증 정보가 유효하지 않은 경우
     * @throws NoSuchElementException   유저 정보가 DB에 존재하지 않는경우
     */
    @GetMapping("/members/me/mypage")
    @ResponseBody
    @Operation(summary = "유저 마이페이지 정보조회 API", description = "마이페이지에 필요한 정보들을 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자의 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Unauthorized\", \"message\": \"로그인 정보가 없습니다.\", \"status\": 401}")))})
    public ResponseEntity<MemberMyPageDto> showMyPage(
            Authentication authentication) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //유저 정보 DB에서 찾아 반환, 닉네임, 코인갯수, 이메일 정보 포함
        MemberMyPageDto memberMyPageInfo = memberService.getMemberMyPageInfo(principal.getName());
        //유저 정보 전
        return ResponseEntity.ok(memberMyPageInfo);
    }

    /**
     * 유저의 닉네임을 수정하는 API 입니다.
     *
     * @param changeNickNameDto 닉네임 변경을 위한 DTO, 유저의 providerId 값과 새로운 닉네임 값을 멤버변수로 가짐
     * @param bindingResult     ChangeNickNameDto 검증 에러를 담는 객체
     * @param authentication    유저의 인증 정보
     * @return ResponseEntity 요청 결과를 담은 응답 객체
     */
    @Operation(summary = "닉네임 변경 API", description = "유저의 닉네임을 변경합니다.")
    @PatchMapping("/members/me/nickname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public ResponseEntity<?> updateNickname(
            @Valid @RequestBody ChangeNickNameDto changeNickNameDto,
            BindingResult bindingResult,
            Authentication authentication) {

        //ChangeNickNameDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("updateNickname API 에러발생 ={}", bindingResult.getFieldError());
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            //에러 메시지 body에 담아서 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보(providerId) 값 저장
        changeNickNameDto.setProviderId(principal.getName());
        //DB에 새로운 닉네임 업데이트
        memberService.updateMemberNickName(changeNickNameDto);

        //새로운 CustomOAuth2User 객체를 만들기 위한 DTO 객체 생성
        MemberOAuthDto memberOAuthDto = MemberOAuthDto.builder()
                .providerId(principal.getName())
                .role(principal.getRole())
                .gender(principal.getGender())
                .nickName(changeNickNameDto.getNewNickName()).build();

        //새로운 Authentication 객체를 만들기 위해 사용될 CustomOAuth2User 객체 생성
        CustomOAuth2User updateUser = new CustomOAuth2User(memberOAuthDto);

        //새로운 Authentication 객체 생성,
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                updateUser,
                authentication.getCredentials(),
                updateUser.getAuthorities()
        );

        // SecurityContextHolder에 새로운 Authentication 객체 설정(유저 인증 정보 업데이트)
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return ResponseEntity.ok("닉네임 변경 성공!");

    }


    /**
     * 유저가 작성한 댓글, 대댓글을 반환하는 API
     * 유저 인증 정보가 올바르면 작성한 댓글,대댓글 정보 반환
     *
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 댓글과 대댓글의 정보 리스트를 담은 응답 객체
     */
    @Operation(summary = "댓글 및 대댓글 조회", description = "사용자가 작성한 댓글 및 대댓글 목록을 조회합니다.",
    parameters = {
            @Parameter(name = "pageNumber", description = "페이지번호 0부터 시작", example = "0"),
            @Parameter(name = "pageSize", description = "페이지 크기 기본값 10", example = "10")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 댓글 및 대댓글 목록을 반환"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                            { "error": "Unauthorized","message": "로그인 정보가 없습니다.","status": 401}
                                    """)))
    })
    @GetMapping("/members/me/comments")
    public ResponseEntity<?> getMemberCommentList(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저가 작성한 댓글,대댓글 정보를 DB에서 받아와 DTO 형태로 변환(최근에 작성한 댓글이 index 앞에 위치)
        List<MemberCommentDto> commentList = memberMyPageService.getMemberCommentAndReCommentList(principal.getName(), pageable);

        //클라이언트로 정보 전송
        return ResponseEntity.ok(commentList);

    }


    /**
     * 인증된 사용자의 선호 소설 목록을 조회하여 반환하는 API입니다.
     *
     * <p>사용자가 인증되었는지 확인한 후, 사용자의 선호 소설을 {@link NovelFavoriteDto}
     * 객체의 목록으로 반환합니다.</p>
     *
     * @param authentication 사용자 인증 정보를 담고 있는 Authentication 객체입니다.
     * @return 인증된 사용자의 선호 소설 목록을 포함하는 ResponseEntity를 반환하며,
     * 인증되지 않은 경우에는 bad request 응답을 반환합니다.
     * @throws AuthenticationCredentialsNotFoundException 유저의 인증정보가 없는경우
     */
    @Operation(summary = "선호 소설 목록 조회", description = "인증된 사용자의 선호 소설 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 선호 소설 목록을 반환"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Unauthorized\", \"message\": \"로그인 정보가 없습니다.\", \"status\": 401}")))
    })
    @GetMapping("/members/me/favorites")
    public ResponseEntity<?> getFavoriteNovels(Authentication authentication) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 좋아요 누른 소설 반환
        List<NovelFavoriteDto> novels = memberMyPageService.getFavoriteNovelsByMember(principal.getName());

        //정보 전송
        return ResponseEntity.ok(novels);

    }


    /**
     * 유저가 코인 사용 기록 열람을 요청하면, body에 담아 전송하는 API 입니다.
     *
     *
     * <p> 인증된 사용자의 코인 사용 내역을
     * 최신순으로 정렬된 {@link MemberCoinUseHistoryDto} 객체 목록으로 반환합니다.</p>
     *
     * @param authentication 사용자 인증 정보를 담고 있는 {@link Authentication} 객체
     * @param pageNumber     페이지 번호를 나타내는 정수값, 기본값은 0
     * @param pageSize       페이지 크기를 나타내는 정수값, 기본값은 10
     * @return 사용자의 코인 사용 기록을 담고 있는 {@link MemberCoinUseHistoryDto} 타입의 {@link List} 객체를
     * 포함한 {@link ResponseEntity}를 반환합니다.
     * @throws IllegalArgumentException                   페이지 번호나 페이지 크기가 유효하지 않을 경우
     * @throws AuthenticationCredentialsNotFoundException 유저 인증정보가 유효하지 않은경우
     */

    @Operation(summary = "코인 사용 기록 조회", description = "인증된 사용자의 코인 사용 내역을 조회합니다."
            , parameters = {
            @Parameter(name = "pageNumber", description = "페이지번호 0부터 시작", example = "0"),
            @Parameter(name = "pageSize", description = "페이지 크기, 기본값 10", example = "10")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 코인 사용 내역을 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberCoinUseHistoryDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Unauthorized\", \"message\": \"로그인 정보가 없습니다.\", \"status\": 401}")))})

    @GetMapping("/members/me/coin-use-history")
    public ResponseEntity<?> getMemberCoinUseHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        //유저 코인 사용 기록 조회
        List<MemberCoinUseHistoryDto> coinUseHistory = memberMyPageService.getMemberCoinUseHistory(principal.getName(), pageable);

        //정보 전송
        return ResponseEntity.ok(coinUseHistory);

    }

    /**
     * 유저가 코인 충전 기록 열람을 요청하면, body에 담아 전송하는 API 입니다.
     *
     * <p> 인증된 사용자의 코인 충전 내역을
     * 최신순으로 정렬된 {@link MemberCoinChargeDto} 객체 목록으로 반환합니다.</p>
     *
     * @param authentication 사용자 인증 정보를 담고 있는 {@link Authentication} 객체
     * @param pageNumber     페이지 번호를 나타내는 정수값, 기본값은 0
     * @param pageSize       페이지 크기를 나타내는 정수값, 기본값은 10
     * @return 사용자의 코인 충전 기록을 담고 있는 {@link MemberCoinChargeDto} 타입의 {@link List} 객체를
     * 포함한 {@link ResponseEntity}를 반환합니다.
     * @throws IllegalArgumentException                   페이지 번호나 페이지 크기가 유효하지 않을 경우
     * @throws AuthenticationCredentialsNotFoundException 유저 인증정보가 유효하지 않은경우
     */

    @Operation(summary = "코인 충전 기록 조회", description = "인증된 사용자의 코인 충전 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 코인 사용 내역을 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberCoinChargeDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Unauthorized\", \"message\": \"로그인 정보가 없습니다.\", \"status\": 401}")))
    })
    @Parameter(name = "pageNumber", description = "페이지번호 0부터 시작", example = "0")
    @Parameter(name = "pageSize", description = "페이지 크기, 기본값 10", example = "10")
    @GetMapping("/members/me/coin-charge-history")
    public ResponseEntity<List<MemberCoinChargeDto>> getMemberCoinChargeHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저 코인 충전 기록 조회
        List<MemberCoinChargeDto> memberCoinChargeHistory = memberMyPageService.getMemberCoinChargeHistory(principal.getName(), pageable);
        //정보 전송
        return ResponseEntity.ok(memberCoinChargeHistory);

    }

    /**
     * 유저가 최근 읽은 작품 기록 열람을 요청하면, body에 담아 전송하는 API입니다.
     *
     * <p>인증된 사용자의 최근 읽은 작품 내역을
     * 최신순으로 정렬된 {@link MemberRecentReadDto} 객체 목록으로 반환합니다.</p>
     *
     * @param authentication 사용자 인증 정보를 담고 있는 {@link Authentication} 객체
     * @param pageNumber     페이지 번호를 나타내는 정수값, 기본값은 0
     * @param pageSize       페이지 크기를 나타내는 정수값, 기본값은 10
     * @return 사용자의 최근 읽은 작품 기록을 담고 있는 {@link MemberRecentReadDto} 타입의 {@link List} 객체를
     * 포함한 {@link ResponseEntity}를 반환합니다.
     * @throws IllegalArgumentException                   페이지 번호나 페이지 크기가 유효하지 않을 경우
     * @throws AuthenticationCredentialsNotFoundException 유저 인증 정보가 유효하지 않을 경우
     */

    @Operation(summary = "최근 읽은 작품 기록 조회", description = "인증된 사용자의 최근 읽은 작품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 최근 읽은 작품 목록을 반환"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자일경우")
    })
    @Parameter(name = "pageNumber", description = "페이지번호 0부터 시작", example = "0")
    @Parameter(name = "pageSize", description = "페이지 크기, 기본값 10", example = "10")
    @GetMapping("/members/me/recent-read")
    public ResponseEntity<List<MemberRecentReadDto>> getMemberRecentRead(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저의 최근 읽은 작품 레코드 조회
        List<MemberRecentReadDto> memberRecentReadInfo = memberMyPageService.getMemberRecentReadInfo(principal.getName(), pageable);

        //정보 전송
        return ResponseEntity.ok(memberRecentReadInfo);
    }

}
