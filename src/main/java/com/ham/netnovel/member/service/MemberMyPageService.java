package com.ham.netnovel.member.service;


import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberMyPageService {


    /**
     * 유저의 댓글과 대댓글 목록을 조회합니다.
     *
     * <p>이 메서드는 주어진 사용자 ID에 대한 댓글과 대댓글을 가져와
     * 두 목록을 합친 후, 생성 시간을 기준으로 최신순으로 정렬하여 반환합니다.</p>
     *
     * @param providerId 유저 정보
     * @param pageable 페이지 정보와 정렬 정보를 담고 있는 {@link Pageable} 객체
     * @return 사용자의 댓글과 대댓글 정보를 담고 있는 {@link MemberCommentDto} 타입의 {@link List} 객체입니다.
     */
    List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId,Pageable pageable);


    /**
     * 사용자의 providerId로, 선호하는 소설 목록을 반환하는 메서드 입니다..
     *
     * <p>주어진 providerId의 유효성을 검사한 후,
     * 해당 사용자가 좋아요를 누른 소설의 목록을 {@link NovelFavoriteDto} 형태로 반환합니다.</p>
     *
     * @param providerId 사용자의 정보 (providerId)
     * @return 사용자가 선호하는 소설의 정보 리스트,
     * {@link NovelFavoriteDto} 타입 객체를  {@link List} 타입으로 반환
     * @throws IllegalArgumentException providerId가 유효하지 않은 경우 예외처리
     */
    List<NovelFavoriteDto> getFavoriteNovelsByMember(String providerId);


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
     * 유저의 최근 열람 기록을 반환하는 메서드 입니다.
     *
     * <p>이 메서드는 사용자의 최근 읽은 소설 목록을 반환하며,
     * 업데이트 시간에 따라 최신순으로 정렬합니다.</p>
     *
     * @param providerId 유저 정보
     * @param pageable 페이지 정보와 정렬 정보를 담고 있는 {@link Pageable} 객체
     * @return 사용자의 최근 읽은 소설 정보를 담고 있는 {@link MemberRecentReadDto} 타입의 {@link List} 객체
     * @throws IllegalArgumentException providerId가 유효하지 않은 경우 예외처리
     */
    List<MemberRecentReadDto> getMemberRecentReadInfo(String providerId, Pageable pageable);







}
