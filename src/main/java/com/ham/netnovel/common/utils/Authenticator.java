package com.ham.netnovel.common.utils;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class Authenticator {

    public CustomOAuth2User checkAuthenticate(Authentication authentication) {

        //authentication 가 null 이거나 인증정보가 없으면 예외로 던짐
        if (authentication==null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        return (CustomOAuth2User) authentication.getPrincipal();


    }


}
