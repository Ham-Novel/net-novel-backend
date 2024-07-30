package com.ham.netnovel.member.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberMyPageDto {

    //닉네임
    private String nickName;

    //코인 갯수
    private Integer coinCount;

    //현재 로그인한 OAuth 이메일
    private String email;



}
