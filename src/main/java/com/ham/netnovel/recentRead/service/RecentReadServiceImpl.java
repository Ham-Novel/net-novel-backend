package com.ham.netnovel.recentRead.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.recentRead.RecentRead;
import com.ham.netnovel.recentRead.RecentReadId;
import com.ham.netnovel.recentRead.RecentReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class RecentReadServiceImpl implements RecentReadService {


    private final RecentReadRepository recentReadRepository;

    private final MemberService memberService;
    private final EpisodeService episodeService;

    public RecentReadServiceImpl(RecentReadRepository recentReadRepository, MemberService memberService, EpisodeService episodeService) {
        this.recentReadRepository = recentReadRepository;
        this.memberService = memberService;
        this.episodeService = episodeService;
    }

    @Override
    @Transactional
    public void createRecentRead(RecentReadId recentReadId, Member member, Episode episode, Novel novel) {
        try {
            //새로운 엔티티 생성
            RecentRead newRecentRead = RecentRead.builder()
                    .id(recentReadId)
                    .episode(episode)
                    .member(member)
                    .novel(novel)
                    .build();
            //엔티티 저장
            recentReadRepository.save(newRecentRead);


        } catch (Exception ex) {
            throw new ServiceMethodException("createRecentRead 메서드 에러 발생: " + ex.getMessage(), ex);
        }


    }

    @Override
    @Transactional
    public void updateRecentRead(String providerId, Long episodeId) {
        //멤버 엔티티 객체에 저장
        Member member = memberService.getMember(providerId)
                .orElseThrow(() -> new NoSuchElementException("Member 가  엔티티가 null 입니다."));
        //에피소드 엔티티 객체에 저장
        Episode episode = episodeService.getEpisodeEntity(episodeId)
                .orElseThrow(() -> new NoSuchElementException("Episode 가 엔티티가 null 입니다."));
        //에피소드의 소설 엔티티 정보 객체에 저장
        Novel novel = episode.getNovel();
        //Novel 엔티티 null체크
        if (novel == null) {
            throw new NoSuchElementException("Novel 엔티티가 null 입니다.");
        }
        //RecentRead Embedded Id 객체 생성
        RecentReadId recentReadId = new RecentReadId(novel.getId(), member.getId());
        //Embedded Id 로 DB에서 유저가 해당 Novel을 열람한 기록이 있는지 확인
        //열람한 기록이 없으면 createRecentRead 호출하여 새로운 엔티티 생성
        //열함한 기록이 있을경우 RecentRead 엔티티 episode 필드값 업데이트
        try {
            recentReadRepository.findById(recentReadId)
                    .ifPresentOrElse(recentRead -> {
                                recentRead.updateEpisodeInfo(episode);//에피소드 필드 업데이트
                                recentReadRepository.save(recentRead);//DB에 저장
                            },
                            () -> createRecentRead(recentReadId, member, episode, novel)//새로운 엔티티 생성후 저장
                    );
        } catch (Exception ex) {
            throw new ServiceMethodException("updateRecentRead 메서드 에러 발생" + ex.getMessage(), ex);
        }


    }


}
