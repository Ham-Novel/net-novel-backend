package com.ham.netnovel.member;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.member.dto.*;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/api")
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
     * 마이페이지 GET 요청시 유저 정보를 전달하는 API
     *
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 유저 정보 담은 응답 객체
     */
    @GetMapping("/members/me/mypage")
    @ResponseBody
    public ResponseEntity<?> showMyPage(
            Authentication authentication
    ) {
        log.info(authentication.getCredentials().toString());
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저 정보 DB에서 찾아 반환, 닉네임, 코인갯수, 이메일 정보 포함
        MemberMyPageDto memberMyPageInfo = memberService.getMemberMyPageInfo(principal.getName());

        //유저 정보 전
        return ResponseEntity.ok(memberMyPageInfo);
    }

    /**
     * 유저의 닉네임을 수정하는 API
     *
     * @param changeNickNameDto 닉네임 변경을 위한 DTO, 유저의 providerId 값과 새로운 닉네임 값을 멤버변수로 가짐
     * @param bindingResult     ChangeNickNameDto 검증 에러를 담는 객체
     * @param authentication    유저의 인증 정보
     * @return ResponseEntity 요청 결과를 담은 응답 객체
     */
    //Todo 송민규: Edit Profile 페이지를 구성하여 Nickname, Email, Gender을 일괄적으로 수정할 수 있도록 기능을 확장하는 건 어떨까요?
    @PatchMapping("/members/me/nickname")
    public ResponseEntity<?> updateNickname(@Valid @RequestBody ChangeNickNameDto changeNickNameDto,
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
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 댓글과 대댓글의 정보 리스트를 담은 응답 객체
     */
    @GetMapping("/members/me/comments")
    public ResponseEntity<?> getMemberCommentList(Authentication authentication,
                                                   @RequestParam(defaultValue = "0") int pageNumber,
                                                   @RequestParam(defaultValue = "10") int pageSize) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저가 작성한 댓글,대댓글 정보를 DB에서 받아와 DTO 형태로 변환(최근에 작성한 댓글이 index 앞에 위치)
        List<MemberCommentDto> commentList = memberMyPageService.getMemberCommentAndReCommentList(principal.getName(),pageable);
        
        //클라이언트로 정보 전송
        return ResponseEntity.ok(commentList);


    }


    /**
     * 유저가 좋아요 누른 소설 리스트를 전송하는 API
     *
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 유저가 좋아요를 누른 소설 리스트를 담은 응답 객체
     */
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
     * 유저가 코인 사용 기록 열람을 요청하면, body에 담아 전송하는 API
     *
     * @param authentication 유저의 인정 정보
     * @return ResponseEntity 데이터를 List에 담아 반환
     */
    @GetMapping("/members/me/coin-use-history")
    public ResponseEntity<?> getMemberCoinUseHistory(Authentication authentication,
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
     * 유저가 코인 충전 기록 열람을 요청하면, body에 담아 전송하는 API
     *
     * @param authentication 유저의 인정 정보
     * @return ResponseEntity 데이터를 List에 담아 반환
     */
    @GetMapping("/members/me/coin-charge-history")
    public ResponseEntity<List<MemberCoinChargeDto>> getMemberCoinChargeHistory(Authentication authentication,
                                                                                 @RequestParam(defaultValue = "0") int pageNumber,
                                                                                 @RequestParam(defaultValue = "10") int pageSize) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저 코인 충전 기록 조회
        List<MemberCoinChargeDto> memberCoinChargeHistory = memberMyPageService.getMemberCoinChargeHistory(principal.getName(),pageable);
        //정보 전송
        return ResponseEntity.ok(memberCoinChargeHistory);

    }

    /**
     *
     * @param authentication
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/members/me/recent-read")
    public ResponseEntity<List<MemberRecentReadDto>> getMemberRecentRead(Authentication authentication,
                                                  @RequestParam(defaultValue = "0") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize){

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //Pageable 객체 생성, null 이거나 음수면 예외로 던짐
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저의 최근 읽은 작품 레코드 조회
        List<MemberRecentReadDto> memberRecentReadInfo = memberMyPageService.getMemberRecentReadInfo(principal.getName(), pageable);

        for (MemberRecentReadDto memberRecentReadDto : memberRecentReadInfo) {

            log.info("정보={}",memberRecentReadDto.toString());
        }
        //정보 전송
        return ResponseEntity.ok(memberRecentReadInfo);
    }


    //테스트 API
    @GetMapping("/member/novel/test")
    public String memberNovelTest() {

        return "/member/novel-test";
    }


    @GetMapping("/members/comment/test")
    public String memberCommentTest() {

        return "/member/comment-test";
    }

    @GetMapping("/members/nickname/test")
    public String nickNameChangeTest() {

        return "/member/nickname-test";
    }

    @GetMapping("/members/session/test")
    @ResponseBody
    public String sessionTest(Authentication authentication) {
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        log.info("로그인한 유저 정보");
        log.info("로그인한 유저 providerId={}", principal.getName());
        log.info("로그인한 유저 nickName={}", principal.getNickName());
        log.info("로그인한 유저 role={}", principal.getRole());
        log.info("로그인한 유저 gender={}", principal.getGender());
        log.info("로그인한 유저 Attributes={}", principal.getAttributes());
        log.info("로그인한 유저 Authorities={}", principal.getAuthorities());

        return "ok";

    }


}
