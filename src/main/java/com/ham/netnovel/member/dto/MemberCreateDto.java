package com.ham.netnovel.member.dto;

import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberCreateDto {

    private String email;

    private OAuthProvider provider;

    //유저 정보 제공자에서 관리하는 유저 Id값
    private String providerId;
    //유저 역할, ADMIN, AUTHOR, READER 3종류
    private MemberRole role;
    //유저 닉네임
    private String nickName;
    //성별
    private Gender gender;
}
