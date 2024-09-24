package com.ham.netnovel.coinUseHistory.service;

import com.ham.netnovel.coinUseHistory.dto.CoinUseCreateDto;
import com.ham.netnovel.coinUseHistory.dto.NovelRevenueDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface CoinUseHistoryService {


    void saveCoinUseHistory(CoinUseCreateDto coinUseCreateDto);


    List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId, Pageable pageable);


    /**
     * 유저가 에피소드에 결제한 내역이 있는지 확인하는 메서드
     *
     * @param providerId 유저 정보
     * @param episodeId  에피소드의 id
     * @return 결제 내역이 있을 경우 true, 결제 내역이 없을경우 false 반환
     */
    boolean hasMemberUsedCoinsForEpisode(String providerId, Long episodeId);


    /**
     * 주어진 소설 ID 목록과 날짜 범위에 대한 코인 사용 내역을 조회하여 반환하는 메서드 입니다.
     *
     * <p>이 메서드는 시작일과 종료일을 기준으로 각 소설에 대해 사용된 코인의 총합을 계산합니다.
     * 결과는 소설 ID, 소설 제목, 작가의 providerId, 총 사용된 코인 수를 포함하는  {@link NovelRevenueDto} 객체로 반환됩니다.
     * </p>
     *
     * @param novelIds 조회할 소설의 ID 목록
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 주어진 날짜 범위에 따른 각 소설의 코인 사용 내역을 포함하는 {@link List} of {@link NovelRevenueDto} 객체
     * @throws ServiceMethodException 조회 중 예외가 발생할 경우
     */
    List<NovelRevenueDto> getCoinUseHistoryByNovelAndDate(List<Long> novelIds,LocalDate startDate, LocalDate endDate);

}
