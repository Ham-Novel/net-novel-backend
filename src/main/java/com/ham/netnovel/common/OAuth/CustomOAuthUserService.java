package com.ham.netnovel.common.OAuth;

import com.ham.netnovel.common.OAuth.dto.NaverOAuthResponse;
import com.ham.netnovel.common.OAuth.dto.OAuth2Response;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.dto.MemberLoginDto;
import com.ham.netnovel.member.dto.MemberOAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuthUserService extends DefaultOAuth2UserService {


    private final MemberService memberService;

    public CustomOAuthUserService(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * OAuth2 제공자에서 보낸 정보 핸들링
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //oAuth2Response 객체 생성
        OAuth2Response oAuth2Response = null;


        switch (registrationId) {
            case "naver":
                log.info("소셜로그인 : naver, 정보={}", oAuth2User.getAttributes());

                oAuth2Response = new NaverOAuthResponse(oAuth2User.getAttributes());

                break;

            case "google":
                log.info("구글");
                break;


            default:
                throw new IllegalArgumentException("유효하지 않은 provider");
        }


        //DB에서 유저 정보 조회
        MemberLoginDto memberLoginInfo = memberService.getMemberLoginInfo(oAuth2Response.getProviderId());

        //반환을 위한 DTO
        MemberOAuthDto memberOAuthDto;

        //DB에 유저 정보가 없을경우, DB에 유저 정보를 저장
        if (memberLoginInfo.getProviderId()==null){
            //OAuth에서 받아온 유저 정보로, 유저 정보 저장을 위한 DTO 생성
            MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                    .email(oAuth2Response.getEmail())
                    .provider(oAuth2Response.getProvider())
                    .providerId(oAuth2Response.getProviderId())
                    .nickName(oAuth2Response.getNickName())
                    .role(MemberRole.READER)
                    .gender(oAuth2Response.getGender())
                    .build();

            //유저 정보 생성
            memberService.createNewMember(memberCreateDto);

            //반환을 위한 DTO 정보 바인딩
            memberOAuthDto = MemberOAuthDto.builder()
                    .providerId(oAuth2Response.getProviderId())
                    .nickName(oAuth2Response.getNickName())
                    .role(MemberRole.READER)
                    .gender(oAuth2Response.getGender())
                    .build();
        }else {//유저 정보가 DB에 있으면, DB값 바탕으로 DTO 반환(닉네임,role 등 변경 가능성 존재)
            //반환을 위한 DTO 정보 바인딩
            memberOAuthDto = MemberOAuthDto.builder()
                    .providerId(memberLoginInfo.getProviderId())
                    .nickName(memberLoginInfo.getNickName())
                    .role(memberLoginInfo.getRole())
                    .gender(memberLoginInfo.getGender())
                    .build();
        }

        return new CustomOAuth2User(memberOAuthDto);
    }
}
