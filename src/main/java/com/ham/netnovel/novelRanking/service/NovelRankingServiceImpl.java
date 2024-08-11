package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.episodeViewCount.EpisodeViewCountService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.NovelRakingRepository;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class NovelRankingServiceImpl implements NovelRankingService {

    private final NovelRakingRepository novelRakingRepository;

    private final EpisodeViewCountService episodeViewCountService;

    private final RedisTemplate<String, String> redisTemplate;

    public NovelRankingServiceImpl(NovelRakingRepository novelRakingRepository, EpisodeViewCountService episodeViewCountService, RedisTemplate<String, String> redisTemplate) {
        this.novelRakingRepository = novelRakingRepository;
        this.episodeViewCountService = episodeViewCountService;

        this.redisTemplate = redisTemplate;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<NovelRanking> getNovelRankingEntity(Long novelId, LocalDate rankingDate, RankingPeriod rankingPeriod) {

        return novelRakingRepository.findByNovelIdAndRankingAndRankingPeriod(novelId, rankingDate, rankingPeriod);

    }

    @Override
    @Transactional
    public void updateDailyRankings() {
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();

        //오늘 날짜로 소설 조회수 랭킹 데이터 가져옴
        List<NovelRankingUpdateDto> todayRanking = episodeViewCountService.getDaliyRanking(todayDate);
        //DB에 저장할 사용할 List 객체 생성
        List<NovelRanking> rankingsToSave = new ArrayList<>();

        for (NovelRankingUpdateDto updateDto : todayRanking) {
            Novel novel = updateDto.getNovel();
            Optional<NovelRanking> novelRankingEntity = getNovelRankingEntity(novel.getId(), todayDate, RankingPeriod.DAILY);
            if (novelRankingEntity.isPresent()) {
                log.info("엔티티 Daily 랭킹 기록 업데이트, Novel Id = {}, 상세정보 ={} ", updateDto.getNovel().getId(), updateDto.toString());
                NovelRanking novelRanking = novelRankingEntity.get();
                novelRanking.updateNovelRanking(updateDto.getRanking(), updateDto.getTotalViews());
                rankingsToSave.add(novelRanking);
            } else {
                log.info("엔티티 Daily 랭킹 기록 생성, Novel Id = {}, 상세정보 ={} ", updateDto.getNovel().getId(), updateDto.toString());
                NovelRanking newNovelRanking = NovelRanking.builder()
                        .rankingPeriod(RankingPeriod.DAILY)
                        .novel(updateDto.getNovel())
                        .totalViews(updateDto.getTotalViews())
                        .rankingDate(todayDate)
                        .ranking(updateDto.getRanking())
                        .build();
                rankingsToSave.add(newNovelRanking);
            }
        }
        novelRakingRepository.saveAll(rankingsToSave);


    }


    @Override
    @Transactional
    public void updateWeeklyRankings() {
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();
        LocalDate endDate = todayDate.minusDays(1);//메서드 실행 날로부터 하루전 정보
        LocalDate startDate = todayDate.minusDays(7);//메서드 실행 날로부터 일주일 전 정보

        List<Object[]> result = novelRakingRepository.findTotalViewsByDateAndRankingPeriod(endDate, startDate, RankingPeriod.DAILY)
                .stream()
                .sorted((obj1, obj2) -> {//결과를 totalViews로 정렬
                    Long totalViews1 = (Long) obj1[1];//인덱스 1번은 totalViews
                    Long totalViews2 = (Long) obj2[1];
                    return totalViews2.compareTo(totalViews1);//totalView 비교, 내림차순으로 정렬
                }).toList();//List로 추출

        //DB에 저장하기 위한 객체 리스트 생성
        List<NovelRanking> rankingsToSave = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            Novel novel = (Novel) result.get(i)[0];//인덱스 0번은 Novel 엔티티
            Long totalViews = (Long) result.get(i)[1];//인덱스 1번은 totalViews
            log.info("엔티티 Weekly 랭킹 기록 생성, Novel Id = {}", novel.getId());
            NovelRanking newNovelRanking = NovelRanking.builder()//NovelRanking 엔티티 생성
                    .rankingPeriod(RankingPeriod.WEEKLY)//주간 랭킹 정보
                    .novel(novel)//소설 엔티티
                    .totalViews(totalViews)//1주간 조회수 총합
                    .rankingDate(todayDate)//저장날짜(오늘)
                    .ranking(i+1)//랭킹순서로 정렬되어 있으므로, i+1이 랭킹
                    .build();
            rankingsToSave.add(newNovelRanking);//리스트에 객체 추가
        }
        novelRakingRepository.saveAll(rankingsToSave);//엔티티 리스트 저장
    }


    @Override
    public void updateMonthlyRankings() {

    }


    @Override
    public void saveRankingToRedis(RankingPeriod rankingPeriod) {

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

        //Redis에 저장할 데이터 객체 생성(정렬된 Set 타입)
        BoundZSetOperations<String, String> zSetOps = redisTemplate.boundZSetOps(key);

        List<NovelRanking> dailyRaking = novelRakingRepository.findByRankingDateAndRankingPeriod(todayDate, rankingPeriod);
        for (NovelRanking novelRanking : dailyRaking) {
            String novelId = novelRanking.getNovel().getId().toString();//novelId 문자열로 변환
            Integer ranking = novelRanking.getRanking();//랭킹 점수 객체에 저장
            redisTemplate.opsForZSet().add(key, novelId, ranking);//novelId 와 ranking 정보를 Redis에 저장
        }
        // redis에  데이터 만료 시간 설정, 1일로 설정
        redisTemplate.expire(key, Duration.ofDays(1));


    }

    @Override
    public List<Map<String, Object>> getRankingFromRedis(String period) {
        // 오늘 날짜로 Redis 키 생성 (예: "daily_rankings:20240810")
        String periodForKey = switch (period) {
            case "daily" -> "daily";
            case "weekly" -> "weekly";
            case "monthly" -> "monthly";
            default -> "all_time";
        };
        //Redis key 생성
        String key = periodForKey + "_rankings:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));


        // Redis에서 Sorted Set의 모든 데이터를 값과 점수와 함께 가져오기
        Set<ZSetOperations.TypedTuple<String>> rankingSet = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        // 결과를 저장할 리스트 초기화
        List<Map<String, Object>> result = new ArrayList<>();

        // 결과가 null이 아닌 경우 처리
        if (rankingSet != null) {
            for (ZSetOperations.TypedTuple<String> item : rankingSet) {
                // novelId와 ranking을 Map으로 저장
                Map<String, Object> entry = new HashMap<>();//List에 저장할 Map 객체 생성
                entry.put("novelId", Long.parseLong(Objects.requireNonNull(item.getValue()))); // novelId는 Long 으로 저장
                entry.put("ranking", Objects.requireNonNull(item.getScore()).intValue()); // ranking은 int로 저장
                result.add(entry);
            }
        }

        //결과 반환
        return result;

    }


}
