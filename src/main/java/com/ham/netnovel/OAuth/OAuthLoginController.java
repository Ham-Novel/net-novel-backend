package com.ham.netnovel.OAuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class OAuthLoginController {


    @GetMapping("/login")
    public String showLoginPage() {
        return "/member/login";
    }
}
