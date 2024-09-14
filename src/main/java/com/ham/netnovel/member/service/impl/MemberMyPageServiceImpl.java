package com.ham.netnovel.member.service.impl;

import com.ham.netnovel.coinChargeHistory.service.CoinChargeHistoryService;
import com.ham.netnovel.coinUseHistory.service.CoinUseHistoryService;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberRecentReadDto;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.member.dto.MemberFavoriteDto;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.reComment.service.ReCommentService;
import com.ham.netnovel.recentRead.service.RecentReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class MemberMyPageServiceImpl implements MemberMyPageService {


    private final CommentService commentService;

    private final ReCommentService reCommentService;

    private final MemberRepository memberRepository;

    private final CoinUseHistoryService coinUseHistoryService;

    private final CoinChargeHistoryService coinChargeHistoryService;

    private final NovelService novelService;

    private final RecentReadService recentReadService;


    public MemberMyPageServiceImpl(CommentService commentService,
                                   ReCommentService reCommentService,
                                   MemberRepository memberRepository,
                                   CoinUseHistoryService coinUseHistoryService,
                                   CoinChargeHistoryService coinChargeHistoryService,
                                   NovelService novelService,
                                   RecentReadService recentReadService) {
        this.commentService = commentService;
        this.reCommentService = reCommentService;
        this.memberRepository = memberRepository;
        this.coinUseHistoryService = coinUseHistoryService;
        this.coinChargeHistoryService = coinChargeHistoryService;
        this.novelService = novelService;
        this.recentReadService = recentReadService;
    }


    @Override
    public List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId, Pageable pageable) {
        // 두 개의 스트림을 합치고 정렬한 후 리스트로 변환하여 반환
        return Stream.concat(
                        //댓글(comment) List를 가져와 stream 생성
                        commentService.getMemberCommentList(providerId, pageable).stream(),
                        //대댓글(reComment) List를 가져와 stream 생성
                        reCommentService.getMemberReCommentList(providerId, pageable).stream()
                )
                //멤버변수인 생성 시간을 기준으로 재정렬
                .sorted(Comparator.comparing(MemberCommentDto::getCreateAt).reversed())
                // 정렬된 스트림을 리스트 형태로 수집하여 반환
                .collect(Collectors.toList());
    }


    //ToDo Author 엔티티 생성 후, 작가 정보도 DTO에 담아서 반환
    @Override
    public List<MemberFavoriteDto> getFavoriteNovelsByMember(String providerId) {
        //유저 providerId 유효성 검사
        validateProviderId(providerId, "getFavoriteNovelsByMember");

        return novelService.getFavoriteNovels(providerId)
                .stream()
                .map(novel -> MemberFavoriteDto.builder()
                        .novelId(novel.getId())
                        .title(novel.getTitle())
                        .status(novel.getType())
                        .episodeCount(novel.getEpisodes().size())
                        .favoriteCount(novel.getFavorites().size())
                        .views(novel.getEpisodes().stream().mapToInt(Episode::getView).sum())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId, Pageable pageable) {
        //유저 providerId 유효성 검사
        validateProviderId(providerId, "getMemberCoinUseHistory");

        return coinUseHistoryService.getMemberCoinUseHistory(providerId, pageable);

    }

    @Override
    public List<MemberCoinChargeDto> getMemberCoinChargeHistory(String providerId, Pageable pageable) {

        //유저 providerId 유효성 검사
        validateProviderId(providerId, "getMemberCoinChargeHistory");
        //유저 정보로 코인 충전 기록을 List로 가져와 반환
        return coinChargeHistoryService.getCoinChargeHistoryByMember(providerId, pageable)
                .stream()
                .sorted(Comparator.comparing(MemberCoinChargeDto::getCreatedAt).reversed())//날짜순으로 정렬(최신 기록이 index 앞에위치)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberRecentReadDto> getMemberRecentReadInfo(String providerId, Pageable pageable) {
        //providerId 값 유효성 검사
        validateProviderId(providerId, "getMemberRecentReadInfo");
        //최근 본 소설 리스트 반환
        return recentReadService.getMemberRecentReads(providerId, pageable)
                .stream()
                .sorted(Comparator.comparing(MemberRecentReadDto::getUpdatedAt).reversed())//업데이트 시간으로 최신순으로 정렬
                .collect(Collectors.toList());
    }

    /**
     * 유저 providerId 값을 검증하는 메서드, null 이거나 비어있으면 예외로 던짐
     *
     * @param providerId 유저 정보
     * @param methodName 검증을 진행하는 메서드 이름
     */
    private void validateProviderId(String providerId, String methodName) {
        if (providerId == null || providerId.trim().isEmpty()) {
            log.error("유저 providerId 값 에러 메서드 명={}", methodName);
            throw new IllegalArgumentException("잘못된 유저 providerId 정보 확인 필요");
        }
    }


}
