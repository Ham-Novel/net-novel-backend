package com.ham.netnovel.settlement.service;


import com.ham.netnovel.coinUseHistory.dto.NovelRevenueDto;
import com.ham.netnovel.settlement.Settlement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public interface SettlementService {




    /**
     * 작성한 소설에 대한 수익 정보를 조회합니다.
     *
     * <p>
     * 이 메서드는 요청한 유저의 정보를 검증하고, 해당 유저가 작성한 소설 ID 목록을 가져온 후,
     * 마지막 정산일로부터 어제까지 각 소설이 벌어들인 코인 수와 수익금액 등의 정보를 DTO 형태로 반환합니다.
     * </p>
     *
     * @param providerId 유저 정보
     * @return 작성한 소설의 수익 정보를 담고 있는 {@link List<NovelRevenueDto>} 객체
     * @throws NoSuchElementException 요청한 유저 정보가 존재하지 않을 경우
     * @throws IllegalArgumentException 작성한 소설이 없을 경우
     */
    List<NovelRevenueDto> getNovelRevenueByAuthor(String providerId);


    /**
     * 지정된 유저의 가장 최근 정산 날짜를 조회합니다.
     *
     * <p>
     * 이 메서드는 주어진 유저의 ID에 대한 최근 정산 날짜를 조회하며,
     * 정산 이력이 없을 경우 기본 정산 날짜를 반환합니다.
     * </p>
     *
     * @param memberId 유저의 고유 식별자
     * @return 유저의 가장 최근 정산 날짜를 나타내는 {@link LocalDateTime} 객체
     * @throws NoSuchElementException 요청한 유저 ID가 존재하지 않을 경우
     */
    LocalDateTime getLatestSettlementDate(Long memberId);


    /**
     * 유저의 정산 요청을 처리하는 메서드입니다.
     *
     * <p>요청이 들어오면 유저 정보를 검증하고, 검증이 완료되면 새로운 {@link Settlement}
     * 엔티티를 생성하여 저장합니다.</p>
     *
     * <p>만약 유저가 이미 정산 요청을 한 상태라면,
     * 새로운 정산 요청을 처리하지 않고 false를 반환하여 메서드를 종료합니다.</p>
     *
     * @param providerId 유저의 고유 식별자 (providerId)
     * @return 정산 요청 처리 성공 여부를 나타내는 boolean 값.
     *         요청이 성공적으로 처리되었으면 true, 이미 요청이 존재하면 false.
     * @throws NoSuchElementException 요청한 유저 정보가 존재하지 않을 경우
     * @throws IllegalArgumentException 유저가 작성한 소설이 없을 경우
     */
    boolean processSettlementRequest(String providerId);


}
