package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.coinUseHistory.CoinUseHistory;
import com.ham.netnovel.coinUseHistory.CoinUseHistoryRepository;
import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoinUseHistoryServiceImpl implements CoinUseHistoryService {

    private final CoinUseHistoryRepository coinUseHistoryRepository;

    private final MemberService memberService;

    private final EpisodeService episodeService;

    public CoinUseHistoryServiceImpl(CoinUseHistoryRepository coinUseHistoryRepository, MemberService memberService, EpisodeService episodeService) {
        this.coinUseHistoryRepository = coinUseHistoryRepository;
        this.memberService = memberService;
        this.episodeService = episodeService;
    }

    @Override
    @Transactional
    public void saveCoinUseHistory(CoinUseCreateDto coinUseCreateDto) {
        log.info("진입");
        //유저 정보 확인
        Member member = memberService.getMember(coinUseCreateDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("Member 정보가 없습니다. providerId: " + coinUseCreateDto.getProviderId()));

        //에피소드 정보 확인
        Episode episode = episodeService.getEpisode(coinUseCreateDto.getEpisodeId())
                .orElseThrow(() -> new NoSuchElementException("Episode 정보가 없습니다. episodeId: " + coinUseCreateDto.getEpisodeId()));

        try {
            //새로운 코인 사용 기록 엔티티 생성
            CoinUseHistory coinUseHistory = CoinUseHistory.builder()
                    .member(member)
                    .episode(episode)
                    .amount(episode.getCoinCost())
                    .build();
            //DB에 저장
            coinUseHistoryRepository.save(coinUseHistory);

        } catch (Exception ex) {
            throw new ServiceMethodException("saveCoinUseHistory 메서드에서 오류 발생", ex);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId) {
        try {
            return coinUseHistoryRepository.findByMemberProviderId(providerId)
                    .stream()//유저의 코인 사용 기록을 받아와 stream 생성
                    .map(coinUseHistory -> {//엔티티 정보 DTO에 바인딩
                                Episode episode = coinUseHistory.getEpisode();
                                return MemberCoinUseHistoryDto.builder()
                                        .episodeTitle(episode.getTitle())//에피소드 제목
                                        .createdAt(coinUseHistory.getCreatedAt())//결제 시간
                                        .usedCoin(coinUseHistory.getAmount())//사용한 코인수
                                        .episodeNumber(episode.getEpisodeNumber())//에피소드 번호
                                        .novelTitle(episode.getNovel().getTitle())//소설 제목
                                        .build();
                            }
                    ).toList();

        } catch (Exception ex) {
            throw new ServiceMethodException("getMemberCoinUseHistory 메서드 에러 발생" + ex.getMessage());
        }


    }
}
