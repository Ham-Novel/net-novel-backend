package com.ham.netnovel.member.service.impl;

import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MemberMyPageServiceImplTest {

    private final MemberMyPageService memberMyPageService;

    @Autowired
    MemberMyPageServiceImplTest(MemberMyPageService memberMyPageService) {
        this.memberMyPageService = memberMyPageService;
    }

    //테스트완료
    @Test
    void getMemberCommentAndReCommentList() {

//                String providerId= "UEqG1Al3FwPQTqDy6tfFb2MGZyEUd-weiJUzyxnkJhM";
        //댓글을 작성한적이 없는 유저 테스트
        String providerId= "ttt";
        List<MemberCommentDto> memberCommentAndReCommentList = memberMyPageService.getMemberCommentAndReCommentList(providerId);
        for (MemberCommentDto memberCommentDto : memberCommentAndReCommentList) {
            System.out.println("댓글");

            System.out.println(memberCommentDto.toString());

        }

    }
}