package com.ham.netnovel.member;


import com.ham.netnovel.OAuth.CustomOAuth2User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class MemberController {


    @GetMapping("/login")
    private String showLoginPage(){


        return "/member/login";


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
