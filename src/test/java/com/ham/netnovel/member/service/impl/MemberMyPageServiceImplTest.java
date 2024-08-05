package com.ham.netnovel.member.service.impl;

import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        //테스트용 계정
                String providerId= "test";

        //댓글을 작성한적이 없는 유저 테스트
//        String providerId = "ttt";
        Pageable pageable = PageRequest.of(0, 10);

        List<MemberCommentDto> memberCommentAndReCommentList = memberMyPageService.getMemberCommentAndReCommentList(providerId,pageable);
        System.out.println("size = "+memberCommentAndReCommentList.size());

        for (MemberCommentDto memberCommentDto : memberCommentAndReCommentList) {
            System.out.println("댓글");

            System.out.println(memberCommentDto.toString());

        }

    }

    //테스트성공
    @Test
    void getFavoriteNovelsByMember() {
        //테스트용 계정
                String providerId= "test";

        //에러 테스트, IllegalArgumentException로 던져짐
//        String providerId = "";


        List<NovelFavoriteDto> favoriteNovelsByMember = memberMyPageService.getFavoriteNovelsByMember(providerId);
        for (NovelFavoriteDto novelFavoriteDto : favoriteNovelsByMember) {
            System.out.println("***** 소설정보 *****");
            System.out.println(novelFavoriteDto.toString());

        }


    }


    //테스트성공
    @Test
    void getMemberCoinUseHistory(){
        //테스트용 계정

        String providerId= "test";
        Pageable pageable = PageRequest.of(0, 10);

        List<MemberCoinUseHistoryDto> memberCoinUseHistory = memberMyPageService.getMemberCoinUseHistory(providerId,pageable);
        System.out.println("List 사이즈 ="+memberCoinUseHistory.size());

        for (MemberCoinUseHistoryDto memberCoinUseHistoryDto : memberCoinUseHistory) {
            System.out.println("***** 코인 사용 기록 *****");
            System.out.println(memberCoinUseHistoryDto.toString());

        }


    }

    //테스트 성공
    @Test
    void getMemberCoinChargeHistory(){
        //테스트용 계정
                String providerId= "test";

        //Null 테스트 IllegalArgumentException로 던져짐
//        String providerId = null;
        Pageable pageable = PageRequest.of(0, 10);

        List<MemberCoinChargeDto> memberCoinChargeHistory = memberMyPageService.getMemberCoinChargeHistory(providerId,pageable);
        System.out.println("List 사이즈 ="+memberCoinChargeHistory.size());
        for (MemberCoinChargeDto memberCoinChargeDto : memberCoinChargeHistory) {
            System.out.println("***** 코인 충전 기록 *****");
            System.out.println(memberCoinChargeDto.toString());

        }


    }

    @Test
    void getMemberRecentReadInfo(){
        //테스트용 계정
        String providerId= "test";
        Pageable pageable = PageRequest.of(0, 10);

        List<MemberRecentReadDto> memberRecentReadInfo = memberMyPageService.getMemberRecentReadInfo(providerId, pageable);
        for (MemberRecentReadDto memberRecentReadDto : memberRecentReadInfo) {
            System.out.println("***** 최근 본 소설 정보 *****");
            System.out.println(memberRecentReadDto.toString());
        }


    }


}