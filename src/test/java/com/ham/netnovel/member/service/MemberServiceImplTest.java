package com.ham.netnovel.member.service;

import com.ham.netnovel.member.dto.ChangeNickNameDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceImplTest {

    private final MemberService memberService;

    @Autowired
    MemberServiceImplTest(MemberService memberService) {
        this.memberService = memberService;
    }


    @Test
    public void updateMemberNickName(){

        ChangeNickNameDto changeNickNameDto = new ChangeNickNameDto();
//        changeNickNameDto.setNewNickName("변경테스트");


        //글자 제한 테스트
        //테스트 완료, 예외로 던져짐
//        changeNickNameDto.setNewNickName("변");

        //잘못된 문자 테스트 (공백, 특수문자 포함)
        //테스트 완료, 예외로 던져짐
//        changeNickNameDto.setNewNickName("변경   테스트");
        changeNickNameDto.setNewNickName("변경@테스트");


        changeNickNameDto.setProviderId("UEqG1Al3FwPQTqDy6tfFb2MGZyEUd-weiJUzyxnkJhM");
        memberService.updateMemberNickName(changeNickNameDto);


    }

    //테스트 완료
    @Test
    public void deductMemberCoins(){

//        String providerId = "";
        //존재하지않은 사용자 테스트
        String providerId = "testtest";//NoSuchElementException로 던져짐

        Integer coinAmount = 3;

        //null체크 테스트
//        Integer coinAmount = null; //IllegalArgumentException로 던져짐
//        Integer coinAmount = 0;


        memberService.deductMemberCoins(providerId,coinAmount);



    }
}