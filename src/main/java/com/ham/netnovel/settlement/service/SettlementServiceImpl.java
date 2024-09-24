package com.ham.netnovel.settlement.service;

import com.ham.netnovel.coinUseHistory.dto.NovelRevenueDto;
import com.ham.netnovel.coinUseHistory.service.CoinUseHistoryService;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.settlement.Settlement;
import com.ham.netnovel.settlement.SettlementRepository;
import com.ham.netnovel.settlement.SettlementStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private static final LocalDateTime DEFAULT_SETTLEMENT_DATE = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final int COIN_PRICE = 100;
    private final MemberService memberService;
    private final NovelService novelService;
    private final SettlementRepository settlementRepository;
    private final CoinUseHistoryService coinUseHistoryService;

    @Autowired
    public SettlementServiceImpl(MemberService memberService, NovelService novelService, SettlementRepository settlementRepository, CoinUseHistoryService coinUseHistoryService) {
        this.memberService = memberService;
        this.novelService = novelService;
        this.settlementRepository = settlementRepository;
        this.coinUseHistoryService = coinUseHistoryService;
    }


    @Override
    @Transactional(readOnly = true)
    public List<NovelRevenueDto> getNovelRevenueByAuthor(String providerId) {

        //요청 유저정보 검증
        Member member = memberService.getMember(providerId)
                .orElseThrow(() ->
                        new NoSuchElementException("calculateRevenueByAuthor 에러, 유저정보가 없습니다. providerId=" + providerId));

        //유저가 생성한 소설 ID 목록을 받아옴
        List<Long> novelIds = novelService.getNovelIdsByAuthor(providerId);

        if (novelIds.isEmpty()) {
            throw new IllegalArgumentException("작성한 소설이 없습니다.");
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);//어제날짜

        //유저가 마지막으로 정산받은 날짜, 정산받은 이력이 없을경우 디폴트값 할당됨
        LocalDate latestSettlementDate = getLatestSettlementDate(member.getId()).toLocalDate();

        //마지막 정산일~ 어제까지 소설별로 얼만큼의 코인을 벌었는지 DTO 에 저장하여 반환
        return coinUseHistoryService.getCoinUseHistoryByNovelAndDate(novelIds, latestSettlementDate, yesterday);
    }

    @Override
    public LocalDateTime getLatestSettlementDate(Long memberId) {
        try {
            return settlementRepository
                    .findLatestSettlementDateByMember(memberId)
                    .orElse(DEFAULT_SETTLEMENT_DATE);
        } catch (Exception ex) {
            throw new ServiceMethodException("getLatestSettlementDate 메서드 에러" + ex + ex.getMessage());

        }

    }


    @Override
    @Transactional
    public boolean processSettlementRequest(String providerId) {
        // providerId가 null이거나 비어있는 경우 예외로 던짐
        if (TypeValidationUtil.isNullOrEmpty(providerId)) {
            throw new IllegalArgumentException("createSettlement 에러, providerId null 이거나 비었습니다..");
        }
        //유저 정보 확인
        Member member = memberService.getMember(providerId)
                .orElseThrow(() ->
                        new NoSuchElementException("createSettlement 에러, 유저정보가 없습니다. providerId=" + providerId));

        //유저가 생성한 소설 ID 목록을 받아옴
        List<Long> novelIds = novelService.getNovelIdsByAuthor(providerId);
        // 유저가 작성한 소설이 없는 경우 예외로 던짐
        if (novelIds.isEmpty()) {
            throw new IllegalArgumentException("작성한 소설이 없습니다.");
        }


        //유저가 정산 요청중인 이력이 있는지 확인, 있으면 true 반환
        //이미 정산 요청중일경우 false 반환하고 메서드 종료
        boolean result = hasRequestedSettlement(member.getId());
        if (result) {
            return false;
        }

        // 유저가 마지막으로 정산받은 날짜를 조회
        // 정산받은 적이 없을 경우 기본값이 할당
        LocalDateTime latestSettlementDate = getLatestSettlementDate(member.getId());


        // 정산 계산 범위의 시작일을 설정
        // 유저가 이전에 정산받은 마지막 정산일 또는 기본값 사용
        LocalDate startDate = latestSettlementDate.toLocalDate();
        // 정산 계산 범위의 종료일을 설정
        // 현재 날짜에서 하루를 뺀 값을 사용
        LocalDate endDate = LocalDate.now().minusDays(1);

        //DB 에서 유저가 작성한 소설이 벌어들인 수익 조회
        List<NovelRevenueDto> novelRevenueDtos = coinUseHistoryService.getCoinUseHistoryByNovelAndDate(novelIds, startDate, endDate);

        // DB에서 조회된 수익 자료로 Settlement 엔티티를 생성
        List<Settlement> settlements = createSettlementEntity(
                novelRevenueDtos,
                member);

        //DB에 Settlement 엔티티 저장
        settlementRepository.saveAll(settlements);
        //true 반환
        return true;
    }


    /**
     * 주어진 소설 수익 정보를 기반으로 {@link Settlement} 엔티티 목록을 생성하는 메서드 입니다.
     *
     * @param novelRevenueDtos 정산할 소설 수익 정보 목록{@link NovelRevenueDto} 객체
     * @param member           정산을 요청한 {@link Member} 객체
     * @return 생성된 정산 {@link Settlement}엔티티 List 객체
     * @throws NoSuchElementException   소설 정보를 찾을 수 없을 때 발생
     * @throws IllegalArgumentException 정산 요청자와 소설 작가가 다를 경우 발생
     */
    private List<Settlement> createSettlementEntity(
            List<NovelRevenueDto> novelRevenueDtos,
            Member member) {
        //반환을 위한 Settlement List 객체 생성
        List<Settlement> settlements = new ArrayList<>();

        for (NovelRevenueDto novelRevenueDto : novelRevenueDtos) {

            //Novel 조회, Null 일경우 예외로 던짐
            Novel novel = novelService.getNovel(novelRevenueDto.getNovelId())
                    .orElseThrow(() -> new NoSuchElementException("createSettlementEntity 에러, Novel 정보가 없습니다."));

            //정산 요청자와 Novel 작가가 동일한지 검증, 다를경우 예외로 던짐
            if (!novel.getAuthor().getProviderId().equals(member.getProviderId())) {
                throw new IllegalArgumentException("createSettlement 메서드 에러, " +
                        "정산 요청자와 소설 작가가 다릅니다. providerId =" + member.getProviderId() + "novelId = " + novel.getId());
            }

            //벌어들인 총 코인수
            Integer totalCoins = novelRevenueDto.getTotalCoins();
            //수익금 계산
            int revenue = totalCoins * COIN_PRICE;

            //Settlement 엔티티 생성
            Settlement build = Settlement.builder()
                    .coinCount(novelRevenueDto.getTotalCoins())//벌어들인 코인수
                    .member(member)
                    .revenue(revenue)//총 수익금
                    .novel(novel)
                    .status(SettlementStatus.REQUESTED)//요청상태
                    .build();

            //List 에 엔티티 추가
            settlements.add(build);
        }
        return settlements;

    }


    /**
     * 유저가 정산을 요청했는지 확인하는 메서드 입니다.
     *
     * @param memberId 정산 요청 여부를 확인할 회원의 ID
     * @return 정산 요청이 있는 경우 {@code true}, 그렇지 않은 경우 {@code false} 반환
     */
    private boolean hasRequestedSettlement(Long memberId) {
        List<Settlement> result = settlementRepository.findRequestedByMember(memberId);
        return !result.isEmpty();
    }

}
