package com.ham.netnovel.coinChargeHistory.service;

import com.ham.netnovel.coinChargeHistory.CoinChargeHistory;
import com.ham.netnovel.coinChargeHistory.CoinChargeHistoryRepository;
import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.dto.MemberCoinChargeDto;
import com.ham.netnovel.member.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CoinChargeHistoryServiceImpl implements CoinChargeHistoryService {

    private final CoinChargeHistoryRepository coinChargeHistoryRepository;

    private final MemberService memberService;

    public CoinChargeHistoryServiceImpl(CoinChargeHistoryRepository coinChargeHistoryRepository, MemberService memberService) {
        this.coinChargeHistoryRepository = coinChargeHistoryRepository;
        this.memberService = memberService;
    }


    @Override
    @Transactional
    public void saveCoinChargeHistory(CoinChargeCreateDto coinChargeCreateDto) {

        //유저 정보 DB 존재유무 확인
        Member member = memberService.getMember(coinChargeCreateDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("saveCoinChargeHistory 메서드 에러, 유저 정보 없음"));

        //구매한 코인수 유효성 검사
        int coinAmount = validateCoinAmount(coinChargeCreateDto.getCoinAmount());

        //지불금액 유효성 검사
        BigDecimal payment = validatePayment(coinChargeCreateDto.getPayment());

        try {
            //엔티티 생성
            CoinChargeHistory coinChargeHistory = CoinChargeHistory.builder()
                    .amount(coinAmount)
                    .payment(payment)
                    .member(member)
                    .build();

            //엔티티 저장
            coinChargeHistoryRepository.save(coinChargeHistory);

            //멤버 엔티티 코인 갯수 변경
            memberService.increaseMemberCoins(member, coinAmount);


        } catch (Exception ex) {
            //그외 예외처리
            throw new ServiceMethodException("saveCoinChargeHistory 메서드 에러 발생" + ex.getMessage()); // 예외 던지기
        }


    }

    @Override
    public List<MemberCoinChargeDto> getCoinChargeHistoryByMember(String providerId, Pageable pageable) {
        try {
            //유저 정보로 DB에서 CoinChargeHistory 레코드를 찾아 DTO로 변환
            return coinChargeHistoryRepository.findByMemberProviderId(providerId,pageable)
                    .stream()
                    .map(coinChargeHistory -> MemberCoinChargeDto.builder()
                            .payment(coinChargeHistory.getPayment())
                            .coinAmount(coinChargeHistory.getAmount())
                            .createdAt(coinChargeHistory.getCreatedAt())
                            .build()).collect(Collectors.toList());

        } catch (Exception ex) {
            throw new ServiceMethodException("getCoinChargeHistoryByMember 메서드 에러 발생" + ex.getMessage()); // 예외 던지기

        }

    }

    //구매한 코인 수 유효성 검사, null 또는 음수여서는 안됨
    //ToDo 최대 코인 갯수 제한
    private int validateCoinAmount(Integer coinAmount) {
        if (coinAmount == null) {
            throw new IllegalArgumentException("saveCoinChargeHistory 메서드 에러 발생, coinAmount 가 null 입니다.");
        } else if (coinAmount <= 0) {
            throw new IllegalArgumentException("saveCoinChargeHistory 메서드 에러 발생, coinAmount 가 음수 입니다.");
        }
        return coinAmount;
    }

    //지불금액 유효성검사, 소수점 2자리 정수부분은 8자리여야함
    //ToDo 최대 결제 금액 제한
    private BigDecimal validatePayment(String payment) {

        //null 체크
        if (payment == null) {
            throw new IllegalArgumentException("payment 가 null 입니다.");
        }
        BigDecimal bigDecimalPayment = new BigDecimal(payment);

        // 음수 값 체크
        if (bigDecimalPayment.signum() == -1) {
            throw new IllegalArgumentException("payment 값은 음수가 될 수 없습니다.");
        }

        // 소수점 이하 자릿수 확인 2자리까지 허용
        if (bigDecimalPayment.scale() > 2) {
            throw new IllegalArgumentException("payment 소수점 자리수가 올바르지 않습니다.");
        }

        // 소수점 자리수 제거
        BigDecimal integerPart = bigDecimalPayment.setScale(0, RoundingMode.DOWN);
        //정수 부분이 8자리인지 확인
        if (!(integerPart.precision() <= 8)) {
            throw new IllegalArgumentException("payment 정수 자리수가 올바르지 않습니다.");
        }

        //변환된 값 반환
        return bigDecimalPayment;
    }
}
