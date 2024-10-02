package com.ham.netnovel.settlement;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {


    /**
     * 유저가 마지막으로 정산 받은 날짜를 반환합니다.
     *
     * <p>이 메서드는 유저의 정산 기록에서, 정산이 완료된(status가 COMPLETED상태인)
     * 정산 기록중 가장 최근 날짜를 반환합니다.
     * </p>
     * <p>만약 유저가 정산을 받은 기록이 없다면, {@code Optional.empty()}를 반환합니다.
     * </p>
     *
     * @param memberId 유저의 고유 식별자 (memberId), null이 아닐 경우에만 유효합니다.
     * @return {@link Optional}로 래핑된 {@link LocalDateTime} 객체.
     * 정산 받은 날짜가 존재하는 경우 그 날짜를 포함하고,
     * 정산 기록이 없는 경우 {@code Optional.empty()}를 반환합니다.
     */
    @Query("select max(st.createdAt) " +
            "from Settlement  st " +
            "where st.member.id = :memberId " +
            "and st.status ='COMPLETED' ")
    Optional<LocalDateTime> findLatestSettlementDateByMember(@Param("memberId") Long memberId);

    /**
     * 유저가 요청한 정산 목록을 반환합니다.
     * <p>
     * 이 메서드는 지정된 유저의 정산 요청 상태가 'REQUESTED'인 모든 {@link Settlement} 엔티티를 조회합니다.
     * 유저의 ID를 통해 정산 요청을 필터링하여 해당 유저가 요청한 정산 내역만 반환합니다.
     * </p>
     *
     * @param memberId 유저의 고유 식별자 (memberId), null이 아닐 경우에만 유효합니다.
     * @return 요청된 정산 내역을 담고 있는 {@link List} 객체.
     * 요청된 정산이 없는 경우 빈 리스트를 반환합니다.
     */
    @Query("select st " +
            "from Settlement st " +
            "where st.member.id = :memberId " +
            "and st.status = 'REQUESTED'  ")
    List<Settlement> findRequestedByMember(@Param("memberId") Long memberId);


    //유저 정보와 페이지네이션 정보로 정산 내역을 조회하는 메서드
    @Query("select  st " +
            "from Settlement  st " +
            "join fetch st.novel n " +
            "where st.member.id = :memberId " +
            "order by st.createdAt desc ")//최근생성일부터
    List<Settlement> findSettlementsByMember(@Param("memberId") Long memberId,
                                             Pageable pageable);


}
