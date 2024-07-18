package com.ham.netnovel.OAuth;

import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;

import java.util.Map;

public class GoogleOAuthResponse implements OAuth2Response{

    private final Map<String,Object> attribute;

    public GoogleOAuthResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
    }

    
    //google은 providerId가 "sub"에 담겨서 전송됨
    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getNickName() {
        return attribute.get("name").toString();
    }

    @Override
    public Gender getGender() {
        return Gender.OTHER;
    }
}
