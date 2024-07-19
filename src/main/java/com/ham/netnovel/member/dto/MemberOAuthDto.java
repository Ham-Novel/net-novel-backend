package com.ham.netnovel.member.dto;


import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOAuthDto {

    //인증 제공자에서 지정한 유저 ID값
    private String providerId;

    //이름(닉네임)
    private String nickName;

    //역할
    private MemberRole role;

    private Gender gender;

}
