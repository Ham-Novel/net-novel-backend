package com.ham.netnovel.utils;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class Authenticator {

    public CustomOAuth2User checkAuthenticate(Authentication authentication) {

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        return (CustomOAuth2User) authentication.getPrincipal();


    }


}
