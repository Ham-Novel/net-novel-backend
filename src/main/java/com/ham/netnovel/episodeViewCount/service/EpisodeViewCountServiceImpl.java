package com.ham.netnovel.episodeViewCount.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episodeViewCount.EpisodeViewCount;
import com.ham.netnovel.episodeViewCount.EpisodeViewCountRepository;
import com.ham.netnovel.episodeViewCount.ViewCountIncreaseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EpisodeViewCountServiceImpl implements EpisodeViewCountService {

    private static final String VIEW_HASH_KEY = "episode:views";

    private final EpisodeViewCountRepository episodeViewCountRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public EpisodeViewCountServiceImpl(EpisodeViewCountRepository episodeViewCountRepository,
                                       @Qualifier("redisCacheTemplate")RedisTemplate<String, String> redisTemplate) {
        this.episodeViewCountRepository = episodeViewCountRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void updateEpisodeViewCountEntity(Map<Long, Episode> episodes, List<ViewCountIncreaseDto> viewCountIncreaseDtos) {

        try {
            //엔티티를 DB에 한번에 저장하기 위한 엔티티 List 객체 생성
            List<EpisodeViewCount> episodeViewCounts = new ArrayList<>();

            LocalDate today = LocalDate.now();//메서드 실행 날짜, DB 검색을 위해 생성

            for (ViewCountIncreaseDto dto : viewCountIncreaseDtos) {
                //DTO의 episodId를 key 값으로, Map 자료형에서 Episode 엔티티 꺼내옴
                Episode episode = episodes.get(dto.getEpisodeId());
                if (episode == null) {
                    log.error("Episode 정보 없음, episodeId={}", dto.getEpisodeId());
                    continue; // 해당 ID를 건너뜀

                }
                EpisodeViewCount episodeViewCount = episodeViewCountRepository
                        .findByEpisodeIdAndViewDate(episode.getId(), today)//DB 에서 EpisodeViewCount 엔티티가 있는지 조회(오늘 날짜로 에피소드 조회 기록이 있는지 확인)
                        .map(EpisodeViewCount -> EpisodeViewCount.increaseViewCount(dto.getViewCount()))//DB에 엔티티가 있으면, 파라미터로 받은 만큼 조회수를 증가시킴
                        .orElseGet(() -> {//DB에 엔티티가 없으면 엔티티를 새로 생성
                                    return EpisodeViewCount.builder()
                                            .viewDate(today)//오늘날짜
                                            .episode(episode)//조회수가 기록될 에피소드
                                            .viewCount(dto.getViewCount())//조회수 저장
                                            .build();

                                }
                        );
                episodeViewCounts.add(episodeViewCount);
                log.info("에피소드 조회수 갱신, episodeId = {}", episode.getId());
            }


            //엔티티 List DB에 저장
            episodeViewCountRepository.saveAll(episodeViewCounts);
            log.info("에피소드 조회수 갱신 완료, 총 에피소드 수 ={} ", episodeViewCounts.size());
            redisTemplate.delete(VIEW_HASH_KEY);//Redis 조회수 정보 초기화


        } catch (Exception ex) {
            throw new ServiceMethodException("updateEpisodeViewCountEntity 메서드 에러" + ex);
        }
    }


    @Transactional
    @Override
    public List<Object[]> getNovelAndNovelTotalViewsByDate(LocalDate startDate, LocalDate endDate){

        try {
            //주어진 기간 사이에 소설의 일간 총조회수를 반환
            return episodeViewCountRepository.findNovelTotalViews(startDate, endDate);

        }catch (Exception ex){
            throw new ServiceMethodException("getNovelAndNovelTotalViewsByDate 메서드 에러, 에러내용 ="+ex+ex.getMessage());
        }



    }

    @Override
    public void incrementEpisodeViewCountInRedis(Long episodeId) {
        //Hash 형태로 Redis에 저장
        redisTemplate.opsForHash().increment(VIEW_HASH_KEY, episodeId.toString(), 1);
    }

    @Override
    public List<ViewCountIncreaseDto> getEpisodeViewCountFromRedis() {
        //Redis로 부터 에피소드 조회수 자료 받아옴
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(VIEW_HASH_KEY);
        //DTO 리스트 반환
        return entries.entrySet()//Map 자료형을 Set 으로 변환
                .stream()
                .map(entry -> {
                    String episodeIdStr = (String) entry.getKey();//episodeId 값 String 으로 변환
                    String viewCountStr = (String) entry.getValue();//viewCount 값 String 으로 변환
                    try {
                        //String으로 변환된 episodeId 값 검증, null 이거나 long 이 아니면 예외로 던짐, 문제없으면  Long 으로 변환
                        Long episodeId = TypeValidationUtil.validateLong(episodeIdStr);
                        //viewCount 값 Integer 로 변환
                        Integer viewCount = Integer.parseInt(viewCountStr);
                        //viewCount 검증 로직 실행, null 이거나 int 타입 범위 초과시 예외로 던짐
                        TypeValidationUtil.validateViewCount(viewCount);

                        return ViewCountIncreaseDto
                                .builder()//DTO로 변환
                                .episodeId(episodeId)//episode Id 값 할당
                                .viewCount(viewCount)//viewCount 값 할당
                                .build();
                    } catch (Exception ex) {
                        throw new ServiceMethodException("getEpisodeViewCountFromRedis 메서드 에러 발생 내용 = " + ex);
                    }

                })


                .collect(Collectors.toList());
    }

};
