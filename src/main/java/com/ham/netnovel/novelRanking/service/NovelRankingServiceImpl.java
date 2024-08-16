package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.episodeViewCount.service.EpisodeViewCountService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.NovelRakingRepository;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        return novelRakingRepository.findByNovelIdAndRankingDateAndRankingPeriod(novelId, rankingDate, rankingPeriod);

    }

    @Override
    @Transactional
    public void updateDailyRankings(LocalDate todayDate) {

        //오늘 날짜로 소설 조회수 랭킹 데이터 가져옴
        List<NovelRankingUpdateDto> todayRanking = episodeViewCountService.getDailyRanking(todayDate);

        //DB에 저장할 사용할 List 객체 생성
        List<NovelRanking> rankingsToSave = new ArrayList<>();

        for (NovelRankingUpdateDto updateDto : todayRanking) {

            Novel novel = updateDto.getNovel();//Novel 엔티티 객체에 저장
            //DB에서 랭킹 엔티티가 있는지 조회
            Optional<NovelRanking> novelRankingEntity = getNovelRankingEntity(novel.getId(), todayDate, RankingPeriod.DAILY);
            //랭킹 엔티티가 있을경우, 랭킹과 점수 필드값만 업데이트
            if (novelRankingEntity.isPresent()) {
                log.info("엔티티 Daily 랭킹 기록 업데이트, Novel Id = {}, 상세정보 ={} ", updateDto.getNovel().getId(), updateDto.toString());
                NovelRanking novelRanking = novelRankingEntity.get();
                novelRanking.updateNovelRanking(updateDto.getRanking(), updateDto.getScore());
                rankingsToSave.add(novelRanking);
            } else {//랭킹 엔티티가 없을경우, 새로 생성하여 DB에 저장
                log.info("엔티티 Daily 랭킹 기록 생성, Novel Id = {}, 상세정보 ={} ", updateDto.getNovel().getId(), updateDto.toString());
                NovelRanking newNovelRanking = NovelRanking.builder()
                        .rankingPeriod(RankingPeriod.DAILY)
                        .novel(updateDto.getNovel())
                        .score(updateDto.getScore())
                        .rankingDate(todayDate)
                        .ranking(updateDto.getRanking())
                        .build();
                rankingsToSave.add(newNovelRanking);
            }
        }
        //모든 엔티티 DB에 업데이트
        novelRakingRepository.saveAll(rankingsToSave);

    }

    //7일전~1일전 일간 점수 합산
    @Override
    @Transactional
    public void updateWeeklyRankings() {
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();
        LocalDate endDate = todayDate.minusDays(1);//메서드 실행 날로부터 하루전 정보
        LocalDate startDate = todayDate.minusDays(7);//메서드 실행 날로부터 일주일 전 정보
        updateNovelRankingEntity(startDate, endDate, todayDate, RankingPeriod.WEEKLY);
    }

    //30일전~1일전 일간 점수 합산
    @Override
    @Transactional
    public void updateMonthlyRankings() {
        //메서드 실행시점 연 월 일 객체에 저장
        LocalDate todayDate = LocalDate.now();
        LocalDate endDate = todayDate.minusDays(1);//메서드 실행 날로부터 하루전 정보
        LocalDate startDate = todayDate.minusDays(30);//메서드 실행 날로부터 30일 전 정보
        updateNovelRankingEntity(startDate, endDate, todayDate, RankingPeriod.MONTHLY);
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

        List<NovelRanking> dailyRaking = novelRakingRepository.findByRankingDateAndRankingPeriod(todayDate, rankingPeriod);
        for (NovelRanking novelRanking : dailyRaking) {
            String novelId = novelRanking.getNovel().getId().toString();//novelId 문자열로 변환
            Integer ranking = novelRanking.getRanking();//랭킹 점수 객체에 저장
            // Redis에 저장할 데이터 객체 생성(정렬된 Set 타입)후 Redis 에 저장
            redisTemplate.opsForZSet().add(key, novelId, ranking);
        }
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


    /**
     * 주어진 기간 동안의 소설 랭킹 정보를 업데이트하는 메서드
     *
     * @param startDate     랭킹을 계산할 시작 날짜
     * @param endDate       랭킹을 계산할 종료 날짜
     * @param todayDate     오늘 날짜, 즉 랭킹을 업데이트할 기준 날짜
     * @param rankingPeriod 랭킹 주기 (일간, 주간, 월간 등)
     */
    private void updateNovelRankingEntity(LocalDate startDate, LocalDate endDate, LocalDate todayDate, RankingPeriod rankingPeriod) {


        //기간동안의 일간 랭킹 정보를 DB에서 찾아 반환
        List<Object[]> rankingInfo = novelRakingRepository.findTotalScoreByDateAndRankingPeriod(startDate, endDate, RankingPeriod.DAILY);

        //오늘 날짜의 소설의 랭킹 엔티티 조회
        //novelId 를 key 로 NovelRanking 엔티티를 value 로 Map 자료형 반환
        Map<Long, NovelRanking> existingRankings = novelRakingRepository.findByRankingDateAndRankingPeriod(todayDate, rankingPeriod)
                .stream()
                .collect(Collectors.toMap(nr -> nr.getNovel().getId(), nr -> nr));

        //DB에 한번에 저장하기 위한 객체 리스트 생성
        List<NovelRanking> rankingsForSaveToDb = new ArrayList<>();

        /*
        반복문을 돌며 랭킹 정보 업데이트
        이미 오늘 날짜로 랭킹 엔티티가 생성되어 있을경우, 엔티티의 랭킹과 조회수 필드값만 수정하여 DB에 저장
        오늘 날짜로 랭킹 엔티티가 생성되어 있지 않으면 새로 생성하여 DB에 저장
         */
        for (int i = 0; i < rankingInfo.size(); i++) {
            Novel novel = (Novel) rankingInfo.get(i)[0];//인덱스 0번은 Novel 엔티티
            Long totalScore = (Long) rankingInfo.get(i)[1];//인덱스 1번은 totalViews

            //오늘날짜의, 소설 랭킹 엔티티가 생성되어있는지 확인(주간,월간,전체)
            NovelRanking novelRanking = existingRankings.get(novel.getId());
            //엔티티가 없으면, 새로운 엔티티 만들어 저장
            if (novelRanking == null) {
                log.info("엔티티 {} 랭킹 기록 생성, Novel Id = {}", rankingPeriod, novel.getId());
                NovelRanking newNovelRanking = NovelRanking.builder()//NovelRanking 엔티티 생성
                        .rankingPeriod(rankingPeriod)//주간 랭킹 정보
                        .novel(novel)//소설 엔티티
                        .score(totalScore)//1주간 조회수 총합//
                        .rankingDate(todayDate)//저장날짜(오늘)
                        .ranking(i + 1)//랭킹순서로 정렬되어 있으므로, i+1이 랭킹
                        .build();
                rankingsForSaveToDb.add(newNovelRanking);//리스트에 객체 추가
            } else {
                //엔티티가 있으면 주간 랭킹 엔티티 업데이트
                log.info("엔티티 {} 랭킹 기록 갱신, Novel Id = {}", rankingPeriod, novel.getId());
                novelRanking.updateNovelRanking(i + 1, totalScore);//랭킹과 조회수 갱신
                rankingsForSaveToDb.add(novelRanking);//리스트에 객체 추가
            }
        }
        novelRakingRepository.saveAll(rankingsForSaveToDb);//엔티티 리스트 DB 에저장
    }


}
