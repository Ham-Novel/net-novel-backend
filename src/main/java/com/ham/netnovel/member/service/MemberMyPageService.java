package com.ham.netnovel.member.service;


import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;

import java.util.List;

public interface MemberMyPageService {


    List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId);

    List<NovelFavoriteDto> getFavoriteNovelsByMember(String providerId);

//    List<FavoriteNovelListDto> getMemberFavoriteNovel


    List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId);


    /**
     * 유저의 코인 충전 기록을 가져오는 메서드
     * 최근 충전 기록이 index 앞에 위치
     * @param providerId 유저 정보
     * @return List MemberCoinChargeDto 형태로 변환해서 반환
     */
    List<MemberCoinChargeDto> getMemberCoinChargeHistory(String providerId);







}
