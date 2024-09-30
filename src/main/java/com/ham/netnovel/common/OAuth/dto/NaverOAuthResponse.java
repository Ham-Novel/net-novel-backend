package com.ham.netnovel.common.OAuth.dto;

import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;

import java.util.Map;

public class NaverOAuthResponse implements  OAuth2Response {

    private final Map<String,Object> attribute;

    public NaverOAuthResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }


    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getNickName() {
        return attribute.get("nickname").toString();
    }

    @Override
    public Gender getGender() {
        if (attribute.get("gender").equals("M")){
            return Gender.MALE;
        }else {
            return Gender.FEMALE;
        }

//        return attribute.get("gender").toString();

    }
}
