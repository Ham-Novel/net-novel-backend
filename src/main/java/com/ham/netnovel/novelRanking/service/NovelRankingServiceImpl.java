package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episodeViewCount.service.EpisodeViewCountService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.NovelRakingRepository;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NovelRankingServiceImpl implements NovelRankingService {

    // 가중치 상수 정의
    private static final int TODAY_VIEW_WEIGHT = 3;
    private static final int YESTERDAY_VIEW_WEIGHT = 2;
    //댓글가중치
    private static final int COMMENT_WEIGHT = 5;
    private final NovelRakingRepository novelRakingRepository;
    private final EpisodeViewCountService episodeViewCountService;
    private final RedisTemplate<String, String> redisTemplate;


    public NovelRankingServiceImpl(NovelRakingRepository novelRakingRepository,
                                   EpisodeViewCountService episodeViewCountService,
                                   @Qualifier("redisCacheTemplate") RedisTemplate<String, String> redisTemplate) {
        this.novelRakingRepository = novelRakingRepository;
        this.episodeViewCountService = episodeViewCountService;

        this.redisTemplate = redisTemplate;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<NovelRanking> getNovelRankingEntity(Long novelId, LocalDate rankingDate, RankingPeriod rankingPeriod) {

        return novelRakingRepository.findNovelRankingsByDateAndPeriod(novelId, rankingDate, rankingPeriod);

    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, NovelRanking> getExistingNovelRankings(List<Long> novelIds,
                                                            LocalDate rankingDate,
                                                            RankingPeriod rankingPeriod) {
        try {
            return novelRakingRepository.findNovelRankingsByDateAndPeriod(novelIds, rankingDate, rankingPeriod)
                    .stream()
                    .collect(Collectors.toMap(
                            novelRanking -> novelRanking.getNovel().getId(),//key는 소설의 id
                            novelRanking -> novelRanking));//value는 NovelRanking 엔티티
        } catch (Exception ex) {
            throw new ServiceMethodException("getExistingNovelRankings 메서드 에러 발생: " + ex.getMessage(), ex);
        }
    }


    @Override
    @Transactional
    public List<NovelRankingUpdateDto> calculateDailyRanking(LocalDate todayDate) {

        try {
            // 어제 날짜 계산
            LocalDate yesterdayDate = todayDate.minusDays(1);
            // 어제와 오늘의 소설 조회수 정보 가져오기
            List<Object[]> novelTotalViews = episodeViewCountService.getNovelAndNovelTotalViewsByDate(yesterdayDate, todayDate);

            //랭킹 정보를 저장할 Map 객체 생성, key는 Novel 엔티티 value는 랭킹 점수
            Map<Novel, Long> scoresOfNovel = new HashMap<>();

            log.info("조회수가 기록된 소설 수 ={}", novelTotalViews.size());

            // 조회수가 있는 소설들의 ID 리스트 추출 (댓글 정보 추출시 사용)
            List<Long> novelIds = novelTotalViews.stream()
                    .map(novelTotalView -> ((Novel) novelTotalView[0]).getId()//인덱스 0번은 Novel 엔티티
                    ).toList();

            // 조회수 기반 점수 계산 및 저장
            calculateNovelViewScores(todayDate, novelTotalViews, scoresOfNovel);
            // 댓글 수 기반 점수 계산 및 저장
            calculateNovelCommentScores(yesterdayDate, todayDate, novelIds, scoresOfNovel);
            //DTO 형태로 변환하여 반환
            return createNovelRankingUpdateDtos(scoresOfNovel);

        } catch (Exception ex) {
            throw new ServiceMethodException("getDailyRanking 메서드 에러 발생: " + ex.getMessage(), ex);
        }

    }

    /**
     * @param scoresOfNovel 소설 엔티티를 key로, 점수를 value로 갖는 Map형 객체
     * @return List<NovelRankingUpdateDto>
     */
    private List<NovelRankingUpdateDto> createNovelRankingUpdateDtos(Map<Novel, Long> scoresOfNovel) {
        //랭킹 정보 주입을 위한 객체 생성
        AtomicInteger ranking = new AtomicInteger(1);
        return scoresOfNovel.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))//점수로 내림차순 정렬
                .map(entry -> NovelRankingUpdateDto.builder()//DTO로 변환
                        .novel(entry.getKey())//0번인덱스 Novel 엔티티
                        .score(entry.getValue())//1번 인덱스 총 조회수
                        .ranking(ranking.getAndIncrement())//현재 값을 사용하고 1 증가
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCommentCountByNovel(List<Long> novelIds, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay(); // 시작일의 00:00:00
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // 종료일의 23:59:59
            //시작일00시~종료일 23시59분까지의 소설 엔티티와, 소설에 달린 댓글 수를 반환
            return novelRakingRepository.findNovelAndCommentCount(novelIds, startDateTime, endDateTime);
        } catch (Exception ex) {
            throw new ServiceMethodException("getCommentCountByNovel 메서드 에러" + ex + ex.getMessage());
        }
    }


    /**
     * 소설의 조회수를 기반으로 점수를 계산하여 scoresOfNovel 맵에 저장합니다.
     *
     * @param todayDate       오늘의 날짜
     * @param novelTotalViews 소설과 해당 소설의 총 조회수 및 조회 날짜를 포함한 리스트 (각 배열의 0번 인덱스는 소설, 1번은 조회수, 2번은 조회 날짜)
     * @param scoresOfNovel   계산된 점수를 저장할 Map 객체 (소설을 키로 하고 점수를 값으로 가짐)
     */
    private void calculateNovelViewScores(LocalDate todayDate,
                                          List<Object[]> novelTotalViews,
                                          Map<Novel, Long> scoresOfNovel) {

        // 조회수를 기반으로 점수를 계산하여 scoresOfNovel에 저장
        for (Object[] novelTotalView : novelTotalViews) {
            log.info("조회수 점수 계산 시작");
            Novel novel = (Novel) novelTotalView[0];//인덱스 0번은 Novel 엔티티
            long views = (((Number) novelTotalView[1]).longValue()); // 총 조회수 (Long으로 처리)
            LocalDate viewDate = (LocalDate) novelTotalView[2];//인덱스 2번은 저장날짜

            // 날짜에 따른 가중치 설정 (오늘은 3, 어제는 2)
            int weight = viewDate.equals(todayDate) ? TODAY_VIEW_WEIGHT : YESTERDAY_VIEW_WEIGHT;
            //가중치와 조회수를 곱하여 점수 계산
            long viewScore = views * weight;
            // Map 자료형에 할당, Novel 값이 있는 경우 기존 value에 score를 더하여 저장
            log.info("조회수 점수 계산, novel ID = {}, viewDate ={} views = {}, scores = {}", novel.getId(), viewDate, views, viewScore);

            scoresOfNovel.merge(novel, viewScore, Long::sum);
        }
    }

    /**
     * 소설의 댓글 수로 점수를 계산하여 총 점수를 업데이트하는 메서드
     *
     * @param novelIds      업데이트할 소설의 ID List
     * @param scoresOfNovel 소설 엔티티와, 점수를 key value 형태로 저장한 Map 객체
     */
    private void calculateNovelCommentScores(LocalDate startDate,
                                             LocalDate endDate,
                                             List<Long> novelIds,
                                             Map<Novel, Long> scoresOfNovel) {
        //댓글 점수 합산하여 저장
        log.info("조회수 점수 계산 시작");
        // 소설별 댓글 수 정보 가져오기
        List<Object[]> commentCountByNovel = getCommentCountByNovel(novelIds, startDate, endDate);
        for (Object[] commentCount : commentCountByNovel) {
            Novel novel = (Novel) commentCount[0];//인덱스 0번은 Novel 엔티티
            long totalComments = (((Number) commentCount[1]).longValue()); // 총 댓글수 (Long으로 처리)
            long commentScore = COMMENT_WEIGHT * totalComments;
            log.info("댓글 점수 계산 결과, novel ID = {}, totalComments={} score = {}", novel.getId(), totalComments, commentScore);
            scoresOfNovel.merge(novel, commentScore, Long::sum);//점수 합산
        }
    }


    @Override
    @Transactional
    public void updateDailyRankings(LocalDate todayDate) {

        // 오늘 날짜에 대한 소설 조회수 랭킹 데이터 객체를 List에 담음
        List<NovelRankingUpdateDto> todayRanking = calculateDailyRanking(todayDate);

        // 오늘 날짜의 소설 ID 목록을 추출
        List<Long> novelIds = todayRanking.stream()
                .map(dto -> dto.getNovel().getId()
                ).toList();

        // 오늘 날짜와 관련된 기존 소설 랭킹 데이터를 DB에서 조회하여 맵에 저장
        Map<Long, NovelRanking> existingRankings = getExistingNovelRankings(novelIds, todayDate, RankingPeriod.DAILY);

        try {
            saveOrUpdateNovelRankings(todayDate, RankingPeriod.DAILY, todayRanking, existingRankings);
        } catch (Exception ex) {
            throw new ServiceMethodException("updateDailyRankings 메서드 에러 발생: " + ex.getMessage(), ex);
        }


    }

    //7일전~1일전 일간 점수 합산
    @Override
    @Transactional
    public void updateWeeklyNovelRankings() {
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();
        LocalDate endDate = todayDate.minusDays(1);//메서드 실행 날로부터 하루전 정보
        LocalDate startDate = todayDate.minusDays(7);//메서드 실행 날로부터 일주일 전 정보
        handleNovelRanking(startDate, endDate, todayDate, RankingPeriod.WEEKLY);
    }


    //30일전~1일전 일간 점수 합산


    @Override
    @Transactional
    public void updateMonthlyNovelRankings() {
        // 메서드 실행 시의 현재 날짜를 todayDate에 저장
        LocalDate todayDate = LocalDate.now();
        // 메서드 실행 날짜 기준으로 하루 전 날짜를 endDate에 저장
        LocalDate endDate = todayDate.minusDays(1);
        // 메서드 실행 날짜 기준으로 30일 전 날짜를 startDate에 저장
        LocalDate startDate = todayDate.minusDays(30);//메서드 실행 날로부터 30일 전 정보
        // 주어진 기간 동안의 소설 랭킹 정보를 처리하는 메서드 호출
        handleNovelRanking(startDate, endDate, todayDate, RankingPeriod.MONTHLY);
    }

    /**
     * 소설의 랭킹 정보를 주어진 기간과 랭킹 주기(일간, 주간, 월간 등)에 따라 계산하고 업데이트하는 메서드.
     * <p>
     * 이 메서드는 주어진 기간 동안의 랭킹 데이터를 가져오고, 새로운 랭킹 정보를 생성한 후
     * 기존 랭킹 정보와 비교하여 DB에 저장 또는 업데이트를 수행합니다.
     * </p>
     *
     * @param startDate     랭킹을 계산할 시작 날짜 (포함)
     * @param endDate       랭킹을 계산할 종료 날짜 (포함)
     * @param todayDate     오늘 날짜, 즉 랭킹을 업데이트할 기준 날짜
     * @param rankingPeriod 랭킹 주기 (일간, 주간, 월간 등), 주기에 따라 다른 랭킹 계산을 수행
     */
    private void handleNovelRanking(LocalDate startDate,
                                    LocalDate endDate,
                                    LocalDate todayDate,
                                    RankingPeriod rankingPeriod) {

        // 주어진 기간 동안, 일간 랭킹 점수의 합산 점수를 소설 엔티티와 함께 반환하는 쿼리 실행
        //인덱스 0번은 Novel 엔티티, 1번은 점수의 총합
        List<Object[]> rankingInfo = novelRakingRepository.findTotalScoreByDateAndRankingPeriod(startDate, endDate, RankingPeriod.DAILY);

        // 반환된 소설과 합산된 랭킹 점수를 저장할 Map 객체 생성
        Map<Novel, Long> scoresOfNovel = rankingInfo.stream()
                .collect(Collectors.toMap(objects -> (Novel) objects[0], objects -> (Long) objects[1]));

        // 반환된 소설들의 ID 값을 저장할 List 객체 생성
        List<Long> novelIds = new ArrayList<>(scoresOfNovel.keySet().stream().map(Novel::getId).toList());

        // NovelRankingUpdateDto 객체들을 생성하여 반환
        List<NovelRankingUpdateDto> novelRankingUpdateDtos = createNovelRankingUpdateDtos(scoresOfNovel);

        // 기존에 DB에 저장된 랭킹 엔티티들을 가져와 Map 형태로 변환, 랭킹 업데이트시 사용
        Map<Long, NovelRanking> existingRankings = getExistingNovelRankings(novelIds, todayDate, rankingPeriod);

        // 랭킹 정보를 저장하거나 업데이트하는 메서드 호출
        saveOrUpdateNovelRankings(todayDate, rankingPeriod, novelRankingUpdateDtos, existingRankings);
    }


    /**
     * 소설 랭킹 엔티티 정보를 저장하거나 업데이트하는 메서드입니다.
     * <p>
     * 이 메서드는 주어진 날짜와 랭킹 주기에 따라 소설 랭킹 정보를 업데이트합니다.
     * 전달된 DTO 리스트를 기반으로, 기존에 저장된 랭킹 엔티티를 조회하고, 해당 엔티티가 존재하지 않으면 새로 생성합니다.
     * </p>
     * <p>
     * 존재하는 엔티티는 업데이트 후, 변경 사항을 데이터베이스에 저장합니다.
     * </p>
     *
     * @param todayDate              오늘 날짜 {@link LocalDate }객체 입니다. 랭킹 정보를 업데이트할 기준 날짜로 사용됩니다.
     * @param rankingPeriod          랭킹 주기입니다. 예를 들어, 일간, 주간, 월간 등의 주기를 설정하는 {@link RankingPeriod} 객체입니다.
     * @param novelRankingUpdateDtos 업데이트할 소설 랭킹 정보가 담긴 DTO 리스트입니다. 각 DTO는 소설의 점수와 랭킹 정보를 포함합니다.
     * @param existingRankings       기존에 데이터베이스에 저장된 랭킹 엔티티를 담고 있는 {@link Map} 객체입니다.
     *                               이 맵의 키는 소설 ID이며, 값은 해당 소설의 랭킹 엔티티입니다.
     */
    private void saveOrUpdateNovelRankings(LocalDate todayDate,
                                           RankingPeriod rankingPeriod,
                                           List<NovelRankingUpdateDto> novelRankingUpdateDtos,
                                           Map<Long, NovelRanking> existingRankings) {
        // 저장할 NovelRanking 엔티티 리스트를 생성

        List<NovelRanking> rankingsToSave = new ArrayList<>();
        for (NovelRankingUpdateDto updateDto : novelRankingUpdateDtos) {
            Novel novel = updateDto.getNovel();//Novel 엔티티 객체에 저장
            // 해당 소설의 기존 랭킹 엔티티를 조회
            NovelRanking novelRankingEntity = existingRankings.get(novel.getId());

            // 엔티티가 없으면, 새로운 NovelRanking 엔티티 생성
            if (novelRankingEntity == null) {
                log.info("엔티티 {} 랭킹 기록 생성, Novel Id = {}", rankingPeriod, novel.getId());
                NovelRanking newNovelRanking = NovelRanking.builder()//NovelRanking 엔티티 생성
                        .rankingPeriod(rankingPeriod) // 랭킹 주기 설정
                        .novel(novel) // 소설 엔티티 설정
                        .score(updateDto.getScore()) // 점수 설정
                        .rankingDate(todayDate) // 오늘 날짜 설정
                        .ranking(updateDto.getRanking()) // 랭킹 설정
                        .build();
                rankingsToSave.add(newNovelRanking);//리스트에 객체 추가
            } else {
                // 엔티티가 존재하면, 기존 랭킹 엔티티를 업데이트
                log.info("엔티티 {} 랭킹 기록 갱신, Novel Id = {}", rankingPeriod, novel.getId());
                novelRankingEntity.updateNovelRanking(updateDto.getRanking(), updateDto.getScore());//랭킹과 조회수 갱신
                rankingsToSave.add(novelRankingEntity);//리스트에 객체 추가
            }
        }
        // 모든 랭킹 엔티티를 DB에 저장
        novelRakingRepository.saveAll(rankingsToSave);

    }


    @Override
    public void saveNovelRankingToRedis(RankingPeriod rankingPeriod) {

        //메서드 실행일 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();

        String periodForKey = switch (rankingPeriod) {
            case WEEKLY -> "weekly";
            case MONTHLY -> "monthly";
            case ALL_TIME -> "all_time";
            default -> "daily";
        };

        //Redis key 생성
        String key = periodForKey + "_rankings:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //랭킹은 200개만 추출해서 Redis에 저장
        Pageable pageable = PageRequest.of(0, 200);

        List<NovelRanking> dailyRaking = novelRakingRepository.findByRankingDateAndRankingPeriod(todayDate, rankingPeriod, pageable);

        //Redis에 한번에 저장기 위한 Set 객체 생성
        Set<ZSetOperations.TypedTuple<String>> rankingSet=
        dailyRaking.stream()
                .map(novelRanking -> new DefaultTypedTuple<>(
                        novelRanking.getNovel().getId().toString(),//novelId 문자열로 변환
                        novelRanking.getRanking().doubleValue()//랭킹 등수 double 형태로 변환
                )).collect(Collectors.toSet());//set 자료형으로 collect

        redisTemplate.opsForZSet().add(key, rankingSet);

        // redis에  데이터 만료 시간 설정, 1일로 설정
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    @Override
    public void deleteNovelRankingInRedis(RankingPeriod rankingPeriod) {

        String periodForKey = switch (rankingPeriod) {
            case WEEKLY -> "weekly";
            case MONTHLY -> "monthly";
            case ALL_TIME -> "all_time";
            default -> "daily";
        };

        String key = periodForKey + "_rankings:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisTemplate.delete(key);


    }

    @Override
    public List<Map<String, Object>> getNovelRankingFromRedis(String period, Integer startIndex, Integer endIndex) {
        // 오늘 날짜로 Redis 키 생성 (예: "daily_rankings:20240810")
        String periodForKey = switch (period) {
            case "weekly" -> "weekly";
            case "monthly" -> "monthly";
            default -> "daily";
        };
        //Redis key 생성
        String key = periodForKey + "_rankings:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Redis에서 Sorted Set의 값과 점수를 가져옴, endIndex 가 Redis 데이터 size 를 벗어나면 index 끝번호까지의 데이터 가져옴
        Set<ZSetOperations.TypedTuple<String>> rankingSet = redisTemplate.opsForZSet().rangeWithScores(key, startIndex, endIndex);

        // 반환할  List 객체 생성
        List<Map<String, Object>> rankingData = new ArrayList<>();

        // 결과가 null이 아닌 경우 처리
        if (rankingSet != null) {
            for (ZSetOperations.TypedTuple<String> item : rankingSet) {
                // novelId와 ranking을 Map으로 저장
                Map<String, Object> entry = new HashMap<>();//List에 저장할 Map 객체 생성
                entry.put("novelId", Long.parseLong(Objects.requireNonNull(item.getValue()))); // novelId는 Long 으로 저장
                entry.put("ranking", Objects.requireNonNull(item.getScore()).intValue()); // ranking은 int로 저장
                rankingData.add(entry);
            }
        }
        //결과 반환
        return rankingData;

    }


}
