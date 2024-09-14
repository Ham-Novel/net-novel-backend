package com.ham.netnovel.member.service;


import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.member.dto.MemberFavoriteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberMyPageService {



    List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId,Pageable pageable);

    List<MemberFavoriteDto> getFavoriteNovelsByMember(String providerId);

//    List<FavoriteNovelListDto> getMemberFavoriteNovel


    /**
     * 유저의 코인 사용 기록을 가져오는 메서드
     * 최근 사용 기록이 index 앞에 위치
     * @param providerId 유저 정보
     * @param pageable 페이지네이션 정보
     * @return List MemberCoinUseHistoryDto 형태로 변환해서 반환
     */
    List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId, Pageable pageable);


    /**
     * 유저의 코인 충전 기록을 가져오는 메서드
     * 최근 충전 기록이 index 앞에 위치
     * @param providerId 유저 정보
     * @param pageable 페이지네이션 정보
     * @return List MemberCoinChargeDto 형태로 변환해서 반환
     */
    List<MemberCoinChargeDto> getMemberCoinChargeHistory(String providerId,Pageable pageable);


    /**
     * 유저의 최근 열람 기록을 반환하는 메서드
     * 소설 정보와, 해당 소설의 최근 열람 에피소드 정보를 반환
     * @param providerId 유저 정보
     * @param pageable
     * @return List MemberRecentReadDto 형태로 반환, 소설, 에피소드 정보를 포함
     */
    List<MemberRecentReadDto> getMemberRecentReadInfo(String providerId, Pageable pageable);







}
