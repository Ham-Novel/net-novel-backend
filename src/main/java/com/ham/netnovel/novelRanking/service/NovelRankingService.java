package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NovelRankingService {

    /**
     * NovelRaking 엔티티를 찾아 반환하는 메서드
     *
     * @param novelId       소설의 ID 값
     * @param rankingDate   랭킹이 기록된 날짜
     * @param rankingPeriod 일간,주간,월간,전체 기간중 해당되는 값
     * @return Optional NovelRanking
     */
    Optional<NovelRanking> getNovelRankingEntity(Long novelId, LocalDate rankingDate, RankingPeriod rankingPeriod);


    /**
     * 주어진 소설 ID 리스트, 랭킹 날짜 및 랭킹 기간에 해당하는 기존 소설 랭킹 정보를 조회합니다.
     *
     * @param novelIds 조회할 소설의 ID 목록
     * @param rankingDate 랭킹을 조회할 날짜
     * @param rankingPeriod 랭킹 기간 (예: 일간, 주간, 월간 등)
     * @return 소설 ID를 키로 하고, 해당 소설의 랭킹 정보를 값으로 가지는 Map 객체
     * @throws ServiceMethodException 랭킹 정보를 조회하는 중 에러가 발생한 경우
     */
    Map<Long, NovelRanking> getExistingNovelRankings(List<Long> novelIds, LocalDate rankingDate, RankingPeriod rankingPeriod);

    /**
     * 주어진 날짜(todayDate)를 기준으로 소설의 일일 랭킹을 계산하여 반환하는 메서드입니다.
     * <p>
     * 이 메서드는 어제와 오늘의 소설 조회수 및 댓글 수를 기반으로 소설의 점수를 계산하고,
     * 해당 점수에 따라 소설의 일일 랭킹을 업데이트합니다.
     * </p>
     *
     * @param todayDate 랭킹을 계산할 기준 날짜입니다. 이 날짜를 기준으로 전날과 당일의 데이터를 사용하여 랭킹을 계산합니다.
     * @return 소설 랭킹 업데이트 정보를 담고 있는 {@link NovelRankingUpdateDto} 객체들의 리스트를 반환합니다.
     *         각 DTO는 소설의 ID와 랭킹 점수를 포함합니다.
     * @throws ServiceMethodException 랭킹 계산 중 발생한 예외를 처리하며, 발생한 예외와 함께 메시지를 포함하여 던집니다.
     *
     */
    List<NovelRankingUpdateDto> calculateDailyRanking(LocalDate todayDate);


    /**
     * 주어진 소설 ID 목록에 대한 댓글 수를 조회하는 메서드입니다.
     *
     * 이 메서드는 주어진 소설 ID 목록에 대해 특정 기간 내에 달린 댓글 수를 조회합니다.
     * 각 소설에 대한 조회 결과는 소설 엔티티 객체와 해당 소설의 댓글 수로 구성된 Object 배열로 반환됩니다.
     *
     * @param novelIds 댓글 수를 조회할 소설의 ID 목록입니다. 이 목록에 포함된 각 소설에 대해 댓글 수를 조회합니다.
     * @param startDate 조회할 댓글의 시작 날짜입니다. 이 날짜를 포함한 이후의 댓글을 조회합니다.
     *                  예를 들어, startDate가 2023-01-01인 경우, 2023년 1월 1일 00:00:00 이후의 댓글이 조회됩니다.
     * @param endDate 조회할 댓글의 종료 날짜입니다. 이 날짜를 포함한 이전의 댓글을 조회합니다.
     *                예를 들어, endDate가 2023-01-31인 경우, 2023년 1월 31일 23:59:59 이전의 댓글이 조회됩니다.
     * @return 주어진 소설 ID 목록에 대한 댓글 수를 포함하는 리스트를 반환합니다.
     *         반환되는 리스트의 각 요소는 소설과 댓글 수를 포함하는 Object 배열입니다.
     *         배열의 첫 번째 요소는 소설 엔티티 객체를 포함하고, 두 번째 요소는 해당 소설의 총 댓글 수를 포함합니다.
     *         예를 들어, 반환된 리스트의 첫 번째 요소가 {novel1, 100}이라면,
     *         이는 novel1 소설에 대해 조회된 기간 내에 총 100개의 댓글이 달렸음을 의미합니다.
     *
     * @throws ServiceMethodException 제공된 소설 ID 목록이 null일 경우, 또는 비어있을 경우 예외를 발생시킬 수 있습니다.
     *                                또한, 조회 중 발생한 다른 예외에 대해서도 이 예외를 발생시킬 수 있습니다.
     */
    List<Object[]> getCommentCountByNovel(List<Long> novelIds,LocalDate startDate, LocalDate endDate);


    /**
     * 지정된 날짜에 대한 소설의 일일 랭킹을 업데이트합니다.
     * <p>
     * 이 메서드는 주어진 날짜에 대한 소설의 일일 랭킹 데이터를 가져와서 기존의 랭킹 엔티티와 비교하고,
     * 필요한 경우 랭킹 레코드를 업데이트하거나 새로 생성합니다. 메서드는 다음 단계를 수행합니다:
     * <ul>
     *     <li>지정된 날짜에 대한 소설 랭킹 데이터를 외부 소스에서 가져옵니다.</li>
     *     <li>지정된 날짜와 관련된 기존 랭킹 레코드를 데이터베이스에서 조회합니다.</li>
     *     <li>일일 랭킹 데이터에 대해 기존 레코드를 업데이트하거나 새로 생성합니다.</li>
     *     <li>모든 업데이트된 또는 새로 생성된 랭킹 엔티티를 데이터베이스에 저장합니다.</li>
     * </ul>
     *
     * @param todayDate 소설 랭킹을 업데이트할 날짜입니다. 이 날짜는 랭킹 데이터를 가져오고,
     *                  랭킹 레코드의 유효성을 결정하는 데 사용됩니다.
     *
     * @throws ServiceMethodException 데이터베이스에 접근하거나 업데이트하는 동안 문제가 발생할 수 있습니다.
     *                             이는 연결 문제, 데이터 무결성 문제 또는 기타 데이터베이스 관련 오류일 수 있습니다.
     */
    void updateDailyRankings(LocalDate todayDate);


    /**
     * 소설의 주간 랭킹을 업데이트합니다.
     * <p>
     * 이 메서드는 실행일 기준으로 7일 전부터 1일 전까지의 일간 랭킹 점수를 합산하여 주간 랭킹을 산출합니다.
     * 메서드는 다음 작업을 수행합니다:
     * <ul>
     *     <li>지정된 날짜를 기준으로 지난 7일 간의 일간 랭킹 점수를 집계하여 랭킹을 계산합니다.</li>
     *     <li>기존 데이터베이스에서 해당 기간의 랭킹 엔티티를 조회합니다.</li>
     *     <li>기존 랭킹 엔티티가 있는 경우에는 조회수와 랭킹을 업데이트합니다.</li>
     *     <li>기존 랭킹 엔티티가 없는 경우에는 새로운 랭킹 엔티티를 생성하여 데이터베이스에 저장합니다.</li>
     * </ul>
     *
     */
    void updateWeeklyNovelRankings();

    /**
     * 소설의 월간 랭킹을 업데이트합니다.
     * <p>
     * 이 메서드는 실행일 기준으로 30일 전부터 1일 전까지의 일간 랭킹 점수를 합산하여 주간 랭킹을 산출합니다.
     * 메서드는 다음 작업을 수행합니다:
     * <ul>
     *     <li>지정된 날짜를 기준으로 지난 30일 간의 일간 랭킹 점수를 집계하여 랭킹을 계산합니다.</li>
     *     <li>기존 데이터베이스에서 해당 기간의 랭킹 엔티티를 조회합니다.</li>
     *     <li>기존 랭킹 엔티티가 있는 경우에는 조회수와 랭킹을 업데이트합니다.</li>
     *     <li>기존 랭킹 엔티티가 없는 경우에는 새로운 랭킹 엔티티를 생성하여 데이터베이스에 저장합니다.</li>
     * </ul>
     *
     */
    void updateMonthlyNovelRankings();


    /**
     * 오늘 날짜의 랭킹을 Redis에 저장하는 메서드
     * @param rankingPeriod daily, weekly, monthly 중 하나의 기간을 파라미터로 받음
     */
    void saveNovelRankingToRedis(RankingPeriod rankingPeriod);


    /**
     * 오늘 날짜의 랭킹을 Redis에서 삭제하는 메서드
     * @param rankingPeriod daily, weekly, monthly 중 하나의 기간을 파라미터로 받음
     */
    void deleteNovelRankingInRedis(RankingPeriod rankingPeriod);


    List<Map<String, Object>> getNovelRankingFromRedis(String period, Integer startIndex, Integer endIndex);


}
