package com.ham.netnovel.recentRead.service;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.recentRead.RecentRead;
import com.ham.netnovel.recentRead.RecentReadId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecentReadService {
    /**
     * 새로운 RecentRead 엔티티를 생성해 DB에 저장하는 매서드
     * @param recentReadId memberId 와 novelId로 이루어진 EmbeddedId
     * @param member 에피소드를 읽은 Member 엔티티 객체
     * @param episode 유저가 읽은 Episode 엔티티 객체
     * @param novel 에피소드가 속한 Novel 엔티티 객체
     */
    void createRecentRead(RecentReadId recentReadId, Member member, Episode episode, Novel novel);


    /**
     * RecentRead 엔티티를 업데이트하는 메서드
     * 유저가 해당 소설을 읽은 기록이 있으면 RecentRead 의 episode_id 업데이트
     * 유저가 해상 소설을 읽은 기록이 없을경우 createRecentRead 호출하여 RecentRead 엔티티 생성하여 DB에 저장
     * @param providerId 에피소드를 읽은 유저의 providerId값
     * @param episodeId 유저가 읽은 에피소드의 Id
     */
    void updateRecentRead(String providerId, Long episodeId);


    /**
     * 유저가 최근 본 에피소드와 에피소드의 소설 정보를 반환하는 메서드
     * @param providerId 유저 정보
     * @param pageable 페이지네이션 보
     * @return List MemberRecentReadDto 유저가 최근본 소설,에피소드 정보를 List로 반환
     */
    List<MemberRecentReadDto> getMemberRecentReads(String providerId, Pageable pageable);



}
