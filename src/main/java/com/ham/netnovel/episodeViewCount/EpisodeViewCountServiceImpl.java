package com.ham.netnovel.episodeViewCount;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novelRanking.dto.NovelRankingUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EpisodeViewCountServiceImpl implements EpisodeViewCountService {

    private final EpisodeViewCountRepository episodeViewCountRepository;


    public EpisodeViewCountServiceImpl(EpisodeViewCountRepository episodeViewCountRepository) {
        this.episodeViewCountRepository = episodeViewCountRepository;
    }

    @Override
    @Transactional
    public void increaseViewCount(Episode episode) {
        try {
            LocalDate today = LocalDate.now();
            //DB에서 엔티티 조회
            EpisodeViewCount episodeViewCount = episodeViewCountRepository.findByEpisodeIdAndViewDate(episode.getId(), today)
                    .map(EpisodeViewCount::increaseViewCount)//DB에 엔티티가 있으면, 조회수 1증가시킴
                    .orElseGet(() -> {//DB에 엔티티가 없으면 엔티티를 새로 생성
                        return EpisodeViewCount.builder()
                                .viewDate(today)
                                .episode(episode)
                                .viewCount(1)//조회수 1로 설정
                                .build();
                    });

            //엔티티 DB에 저장
            episodeViewCountRepository.save(episodeViewCount);
            log.info("에피소드 조회수 1 증가 성공, episodeId = {}", episode.getId());

        } catch (Exception ex) {
            throw new ServiceMethodException("에러" + ex.getMessage());
        }


    }

    @Override
    @Transactional
    public List<NovelRankingUpdateDto> getDaliyRanking(LocalDate todayDate) {
        try {
            //해당 날짜의 Novel 총 조회수를 계산하여 DTO List 객체에 담음
            List<NovelRankingUpdateDto> rankingUpdateDtos = episodeViewCountRepository.findTodayNovelTotalViews(todayDate)
                    .stream()
                    .map(objects -> {
                        return NovelRankingUpdateDto.builder()
                                .novel((Novel) objects[0])//0번인덱스 Novel 엔티티
                                .totalViews((Long) objects[1])//1번 인덱스 총 조회수
                                .build();
                    })
                    .sorted(Comparator.comparing(NovelRankingUpdateDto::getTotalViews).reversed())//조회수 기준으로 내림차순 정렬
                    .toList();

            //조회수 순서로 정렬된 DTO에 랭킹 정보 추가
            for (int i = 0; i < rankingUpdateDtos.size(); i++) {
                rankingUpdateDtos.get(i).setRanking(i + 1);
            }
            //반환
            return rankingUpdateDtos;

        } catch (Exception ex) {
            throw new ServiceMethodException("getDaliyRanking 메서드 에러 발생 내용 = " + ex.getMessage());
        }

    }

    @Override
    @Transactional
    public List<NovelRankingUpdateDto> getWeeklyRanking(LocalDate todayDate) {
        try {
        LocalDate yesterday = todayDate.minusDays(1);//메서드 실행 날로부터 하루전 정보
        LocalDate startDate = todayDate.minusDays(7);//메서드 실행 날로부터 일주일 전 정보
        List<NovelRankingUpdateDto> rankingUpdateDtos = episodeViewCountRepository.findWeeklyNovelTotalViews(yesterday, startDate)
                .stream()
                .map(objects -> {
                            return NovelRankingUpdateDto.builder()
                                    .novel((Novel) objects[0])//0번인덱스 Novel 엔티티
                                    .totalViews((Long) objects[1])//1번 인덱스 총 조회수
                                    .build();
                        }
                ).sorted(Comparator.comparing(NovelRankingUpdateDto::getTotalViews).reversed())//조회수 기준으로 내림차순 정렬
                .toList();

        //조회수 순서로 정렬된 DTO에 랭킹 정보 추가
        for (int i = 0; i < rankingUpdateDtos.size(); i++) {
            rankingUpdateDtos.get(i).setRanking(i + 1);

        }
        return rankingUpdateDtos;
        } catch (Exception ex) {
            throw new ServiceMethodException("getWeeklyRanking 메서드 에러 발생 내용 = " + ex.getMessage());
        }

    }
};
