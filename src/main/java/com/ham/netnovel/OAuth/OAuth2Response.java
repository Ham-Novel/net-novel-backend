package com.ham.netnovel.OAuth;


import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;

public interface OAuth2Response {

    /**
     * 콘텐츠 제공자를 반환합니다. 예: google, naver
     * @return String
     */
    OAuthProvider getProvider();

    /**
     * 콘텐츠 제공자가 전달한 Id 값입니다.
     * @return String
     */
    String getProviderId();

    /**
     * 콘텐츠 제공자가 전달한 유저 이메일 입니다.
     * @return String
     */
    String getEmail();


    /**
     * 콘텐츠 제공자가 전달한 유저 이름입니다.
     * @return String
     */
    String getNickName();


    Gender getGender();





}
