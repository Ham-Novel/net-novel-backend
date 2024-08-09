package com.ham.netnovel.novelRanking.service;

import com.ham.netnovel.episodeViewCount.EpisodeViewCountService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.NovelRakingRepository;
import com.ham.netnovel.novelRanking.NovelRanking;
import com.ham.netnovel.novelRanking.RankingPeriod;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NovelRankingServiceImpl implements NovelRankingService {

    private final NovelRakingRepository novelRakingRepository;

    private final EpisodeViewCountService episodeViewCountService;

    public NovelRankingServiceImpl(NovelRakingRepository novelRakingRepository, EpisodeViewCountService episodeViewCountService) {
        this.novelRakingRepository = novelRakingRepository;
        this.episodeViewCountService = episodeViewCountService;

    }


    @Override
    @Transactional(readOnly = true)
    public Optional<NovelRanking> getNovelRankingEntity(Long novelId, LocalDate rankingDate, RankingPeriod rankingPeriod) {

        return novelRakingRepository.findByNovelIdAndRankingAndRankingPeriod(novelId,rankingDate,rankingPeriod);

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
            if (novelRankingEntity.isPresent()){
                log.info("엔티티 Daily 랭킹 기록 업데이트, Novel Id = {}, 상세정보 ={} ",updateDto.getNovel().getId(),updateDto.toString());
                NovelRanking novelRanking = novelRankingEntity.get();
                novelRanking.updateNovelRanking(updateDto.getRanking(), updateDto.getTotalViews());
                rankingsToSave.add(novelRanking);
            } else {
                log.info("엔티티 Daily 랭킹 기록 생성, Novel Id = {}, 상세정보 ={} ",updateDto.getNovel().getId(),updateDto.toString());
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
        //오늘 날짜로 소설 조회수 랭킹 데이터 가져옴
        List<NovelRankingUpdateDto> weeklyRanking = episodeViewCountService.getWeeklyRanking(todayDate);
        //DB에 저장할 사용할 List 객체 생성
        List<NovelRanking> rankingsToSave = new ArrayList<>();

        for (NovelRankingUpdateDto updateDto : weeklyRanking) {
            Novel novel = updateDto.getNovel();
            Optional<NovelRanking> novelRankingEntity = getNovelRankingEntity(novel.getId(), todayDate, RankingPeriod.WEEKLEY);

            if (novelRankingEntity.isPresent()){
                log.info("엔티티 Weekly 랭킹 기록 업데이트, Novel Id = {}, 상세정보 ={} ",updateDto.getNovel().getId(),updateDto.toString());
                NovelRanking novelRanking = novelRankingEntity.get();
                novelRanking.updateNovelRanking(updateDto.getRanking(), updateDto.getTotalViews());
                rankingsToSave.add(novelRanking);
            } else {
                log.info("엔티티 Weekly 랭킹 기록 생성, Novel Id = {}, 상세정보 ={} ",updateDto.getNovel().getId(),updateDto.toString());
                NovelRanking newNovelRanking = NovelRanking.builder()
                        .rankingPeriod(RankingPeriod.WEEKLEY)
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
    public void updateMonthlyRankings() {

    }
}
