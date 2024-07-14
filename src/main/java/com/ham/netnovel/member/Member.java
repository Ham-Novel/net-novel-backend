package com.ham.netnovel.member;


import jakarta.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;



//  유저 email, naver는 설정값에 따라 도메인이 naver가 아닐수도 있음
    @Column(unique = true)
    private String email;


    //유저 정보 제공자,(naver,kakao등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;


    //유저 정보 제공자에서 관리하는 유저 Id값
    @Column(unique = true)
    private String providerId;

    //유저 닉네임
    private String nickName;


}
