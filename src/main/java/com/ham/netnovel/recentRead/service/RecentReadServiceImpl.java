package com.ham.netnovel.recentRead.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.recentRead.RecentRead;
import com.ham.netnovel.recentRead.RecentReadId;
import com.ham.netnovel.recentRead.RecentReadRepository;
import com.ham.netnovel.s3.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecentReadServiceImpl implements RecentReadService {


    private final RecentReadRepository recentReadRepository;

    private final MemberService memberService;
    private final EpisodeService episodeService;
    private final S3Service s3Service;

    public RecentReadServiceImpl(RecentReadRepository recentReadRepository, MemberService memberService, EpisodeService episodeService, S3Service s3Service) {
        this.recentReadRepository = recentReadRepository;
        this.memberService = memberService;
        this.episodeService = episodeService;
        this.s3Service = s3Service;
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
        Episode episode = episodeService.getEpisode(episodeId)
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

    @Override
    @Transactional(readOnly = true)
    public List<MemberRecentReadDto> getMemberRecentReads(String providerId, Pageable pageable) {
        try {
            return recentReadRepository.findByMemberProviderId(providerId, pageable)
                    .stream()
                    .map(recentRead -> {
                        Episode episode = recentRead.getEpisode();//에피소드 엔티티 객체에 저장
                        Novel novel = recentRead.getNovel();//노벨 엔티티 객체에 저장
                        return MemberRecentReadDto.builder()//DTO로 변환하여 반환
                                .novelId(novel.getId())
                                .novelTitle(novel.getTitle())
                                .novelDesc(novel.getDescription())
                                .novelType(novel.getType())
                                .authorName(novel.getAuthor().getNickName())
                                .thumbnailUrl(
                                        s3Service.generateCloudFrontUrl(novel.getThumbnailFileName(),"mini")
                                )
                                .tags(
                                        novel.getNovelTags().stream()
                                        .map(novelTag -> novelTag.getTag().getData())
                                        .toList()
                                )
                                .episodeTitle(episode.getTitle())
                                .episodeId(episode.getId())
                                .updatedAt(recentRead.getUpdatedAt())
                                .build();
                    })
                    .collect(Collectors.toList());//List로 collect



        } catch (Exception ex) {
            throw new ServiceMethodException("getMemberRecentReads 메서드 에러 발생" + ex.getMessage(), ex);
        }

    }


}
