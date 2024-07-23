package com.ham.netnovel.member;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.member.dto.ChangeNickNameDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.member.dto.MemberOAuthDto;
import com.ham.netnovel.common.utils.Authenticator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
public class MemberController {

    private final Authenticator authenticator;

    private final MemberService memberService;

    private final MemberMyPageService memberMyPageService;

    public MemberController(Authenticator authenticator, MemberService memberService, MemberMyPageService memberMyPageService) {
        this.authenticator = authenticator;
        this.memberService = memberService;
        this.memberMyPageService = memberMyPageService;
    }


    @GetMapping("/login")
    public String showLoginPage() {

        return "/member/login";


    }

    /**
     * 유저의 닉네임을 수정하는 API
     * @param changeNickNameDto 닉네임 변경을 위한 DTO, 유저의 providerId값과 새로운 닉네임 값을 멤버변수로 가짐
     * @param bindingResult ChangeNickNameDto 검증 에러를 담는 객체
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 결과를 헤더에 담아 전송
     */
    @PatchMapping("/members/nickname")
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
     * API 요청시 인증 정보를 확인 한 후, 인증된 유저가 작성한 댓글,대댓글을 DTO 변환후 List 형태로 반환
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 댓글과 대댓글의 정보를 body에 담아 반환
     */
    @PostMapping("/members/comment")
    public ResponseEntity<?> postMemberCommentList(Authentication authentication){

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 작성한 댓글,대댓글 정보를 DB에서 받아와 DTO 형태로 변환(최근에 작성한 댓글이 index 앞에 위치)
        List<MemberCommentDto> commentList = memberMyPageService.getMemberCommentAndReCommentList(principal.getName());

        for (MemberCommentDto memberCommentDto : commentList) {
            log.info("정보{}",memberCommentDto.toString());

        }

        //클라이언트로 정보 전송
        return ResponseEntity.ok(commentList);


    }





    @GetMapping("/members/comment/test")
    public String memberCommentTest(){

        return "/member/comment-test";
    }

    @GetMapping("/members/nickname/test")
    public String nickNameChangeTest(){

        return "/member/nickname-test";
    }

    @GetMapping("/members/session/test")
    @ResponseBody
    public String sessionTest(Authentication authentication){
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        log.info("로그인한 유저 정보");
        log.info("로그인한 유저 providerId={}",principal.getName());
        log.info("로그인한 유저 nickName={}",principal.getNickName());
        log.info("로그인한 유저 role={}",principal.getRole());
        log.info("로그인한 유저 gender={}",principal.getGender());
        log.info("로그인한 유저 Attributes={}",principal.getAttributes());
        log.info("로그인한 유저 Authorities={}",principal.getAuthorities());

        return "ok";

    }


//    @GetMapping("/mypage")
//    @ResponseBody
//    public String showMyPage(Authentication authentication){
//       if (authentication.isAuthenticated()){
//       CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
//           log.info(principal.getNickName());
//       }
//
//        return "ok";
//
//    }

}
