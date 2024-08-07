package com.ham.netnovel.OAuth;

import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.dto.MemberOAuthDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, Serializable {

    private final MemberOAuthDto memberOAuthDto;

    public CustomOAuth2User(MemberOAuthDto memberOAuthDto) {
        this.memberOAuthDto = memberOAuthDto;
    }


    //    제공자별로 형식이 다릅니다. 사용금지
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> String.valueOf(memberOAuthDto.getRole()));

        return collection;
    }

    //현재 프로젝트에서 ProviderId를 name으로 사용중입니다.
    @Override
    public String getName() {
        return memberOAuthDto.getProviderId();
    }


    public String getNickName(){
        return memberOAuthDto.getNickName();
    }

    public MemberRole getRole(){
        return memberOAuthDto.getRole();
    }

    public Gender getGender(){
        return memberOAuthDto.getGender();
    }
}
