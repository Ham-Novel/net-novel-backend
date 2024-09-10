package com.ham.netnovel.common.batch.job;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.repository.NovelRepository;
import com.ham.netnovel.novelMetaData.NovelMetaData;
import com.ham.netnovel.novelMetaData.data.MetaDataType;
import com.ham.netnovel.novelMetaData.service.NovelMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@EnableBatchProcessing
@Slf4j
public class NovelMetaDataBatchConfig {


    private static final long DEFAULT_VIEWS = 0L;//조회수 기본값
    private static final int DEFAULT_FAVORITES = 0;//좋아요수 기본값
    //날짜 기본값
    private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);
    private final NovelMetaDataRepository novelMetaDataRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final NovelRepository novelRepository;


    public NovelMetaDataBatchConfig(NovelMetaDataRepository novelMetaDataRepository, JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, NovelRepository novelRepository) {
        this.novelMetaDataRepository = novelMetaDataRepository;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.novelRepository = novelRepository;
    }

    //조회수 업데이트 Job
    @Bean
    public Job novelTotalViewUpdateJob() {
        return new JobBuilder("novelTotalViewUpdateJob", jobRepository)
                .start(updateNovelTotalViewsStep())//스탭자리
                .build();//
    }

    //좋아요수 업데이트 Job
    @Bean
    public Job novelTotalFavoritesUpdateJob() {
        return new JobBuilder("novelTotalFavoritesUpdateJob", jobRepository)
                .start(updateNovelFavoritesStep())//스탭자리
                .build();//

    }

    //최근 업데이트 날짜 갱신 Job
    @Bean
    public Job novelLatestEpisodeAtUpdateJob() {
        return new JobBuilder("novelLatestEpisodeAtUpdateJob", jobRepository)
                .start(updateNovelLatestEpisodeAtStep())//스탭자리
                .build();//
    }

    @Bean
    public Step updateNovelTotalViewsStep() {
        return new StepBuilder("totalViewsStep", jobRepository)
                .<Novel, NovelMetaData>chunk(100, platformTransactionManager)//10개 10초 100개 11초 1000개 45초
                .reader(novelReader())//Novel 레코드를 읽어오는 reader 메서드
                .processor(novelTotalViewsProcessor())//조회수 업데이트 처리 메서드
                .writer(novelMetaDataItemWriter())//NovelMetaData를 DB에 저장하는 메서드
                .build();
    }

    @Bean
    public Step updateNovelFavoritesStep() {
        return new StepBuilder("totalFavoritesStep", jobRepository)
                .<Novel, NovelMetaData>chunk(100, platformTransactionManager)
                .reader(novelReader())//Novel 레코드를 읽어오는 reader 메서드
                .processor(novelFavoritesProcessor())//좋아요수 업데이트 처리 메서드
                .writer(novelMetaDataItemWriter())//NovelMetaData를 DB에 저장하는 메서드
                .build();
    }

    @Bean
    public Step updateNovelLatestEpisodeAtStep() {
        return new StepBuilder("latestEpisodeAtStep", jobRepository)
                .<Novel, NovelMetaData>chunk(100, platformTransactionManager)
                .reader(novelReader())//Novel 레코드를 읽어오는 reader 메서드
                .processor(novelLatestEpisodeAtProcessor())//에피소드 최신 업데이트 날짜 처리 메서드
                .writer(novelMetaDataItemWriter())//NovelMetaData를 DB에 저장하는 메서드
                .build();
    }


    //Novel 레코드를 페이지네이션 정보를 이용하여 읽어오는 Reader 메서드
    @Bean
    public RepositoryItemReader<Novel> novelReader() {
        try {
            return new RepositoryItemReaderBuilder<Novel>()
                .name("novelReader")
                .methodName("findAll")//DAO 계층에서 사용할 메서드 이름 입력
                .pageSize(100)//Step의 chunk와 동일한 값 입력 필요
                .repository(novelRepository)//Reader 메서드에서 사용할 DAO 클래스 객체 할당
                .sorts(Map.of("id", Sort.Direction.ASC))//ID로 오름차순 정렬
                .build();
        }catch (Exception ex){
            log.error("Novel 불러오던중 에러 발생, 에러내용 ={}",ex.getMessage());
            throw ex;
        }

    }

    //소설의 총 조회수를 계산하여 반환하는 Processor 메서드
    @Bean
    public ItemProcessor<Novel, NovelMetaData> novelTotalViewsProcessor() {
        return novel -> {
            try {
                //소설에 에피소드가 있을경우, 소설의 에피소드의 조회수를 총합하여 객체에 할당
                long totalViews = novel.getEpisodes()
                        .stream()
                        .map(Episode::getView)
                        .filter(Objects::nonNull)//Null체크
                        .mapToLong(Integer::longValue)//Long으로 변환
                        .sum();//합산 비어있을경우 0 반환
                //새로운 NovelMetaData 만들거나 업데이트
                return updateOrCreateNovelMetaDate(novel, MetaDataType.VIEW, totalViews, DEFAULT_FAVORITES, DEFAULT_DATE);

            } catch (Exception ex) {
                log.error("소설 메타데이터  조회수 갱신 실패, novelId ={}, 예외내용 = {}", novel.getId(), ex.getMessage());
                throw ex;
            }
        };
    }

    @Bean
    public ItemProcessor<Novel, NovelMetaData> novelFavoritesProcessor() {
        return novel -> {

            try {
                //좋아요가 없을경우 엔티티에 저장할 DEFAULT 셋팅
                int totalFavorites = DEFAULT_FAVORITES;
                //소설에 좋아요가 있을경우, 소설의 에피소드를 총합하여 객체에 할당
                if (!novel.getFavorites().isEmpty()) {
                    totalFavorites = novel.getFavorites().size();
                }
                //새로운 NovelMetaData 만들거나 업데이트
                return updateOrCreateNovelMetaDate(novel, MetaDataType.FAVORITE, DEFAULT_VIEWS, totalFavorites, DEFAULT_DATE);
            } catch (Exception ex) {
                log.error("소설 메타데이터 좋아요 갱신 실패, novelId ={}, 예외내용 = {}", novel.getId(), ex.getMessage());
                throw ex;
            }
        };
    }

    @Bean
    public ItemProcessor<Novel, NovelMetaData> novelLatestEpisodeAtProcessor() {
        return novel -> {
            try {
                //소설에 에피소드가 있을경우, 소설의 가장 최근 에피소드의 생성일을 객체에 할당
                LocalDateTime latestAt = novel.getEpisodes()
                        .stream()
                        .map(Episode::getCreatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(DEFAULT_DATE);


                //새로운 NovelMetaData 만들거나 업데이트
                return updateOrCreateNovelMetaDate(novel,
                        MetaDataType.DATE,
                        DEFAULT_VIEWS,
                        DEFAULT_FAVORITES,
                        latestAt);
            } catch (Exception ex) {
                log.error("소설 메타데이터 최근업데이트날짜 갱신 실패, novelId ={}, 예외내용 = {}", novel.getId(), ex.getMessage());
                throw ex;
            }

        };


    }

    private NovelMetaData updateOrCreateNovelMetaDate(Novel novel,
                                                      MetaDataType type,
                                                      Long totalViews,
                                                      int totalFavorites,
                                                      LocalDateTime latestDate) {

        return novelMetaDataRepository.findByNovel(novel)
                .map(novelMetaData -> {
                    //타입에 맞춰 필드값 업데이트
                    switch (type) {
                        case FAVORITE -> novelMetaData.updateTotalFavorites(totalFavorites);
                        case VIEW -> novelMetaData.updateTotalViews(totalViews);
                        case DATE -> novelMetaData.updatedLatestEpisodeAt(latestDate);
                        default ->
                                throw new ServiceMethodException("updateOrCreateNovelMetaDate 에러, type이 유효하지 않습니다. type: " + type);
                    }
                    //반환
                    return novelMetaData;

                    //만약 Null일경우 새로운 객체 생성하여 반환
                }).orElseGet(() -> NovelMetaData.builder()
                        .totalFavorites(totalFavorites)
                        .novel(novel)
                        .totalViews(totalViews)
                        .latestEpisodeAt(latestDate)
                        .build());

    }


    //MetaData Writer, DB에 저장
    @Bean
    public RepositoryItemWriter<NovelMetaData> novelMetaDataItemWriter() {
        try {
            return new RepositoryItemWriterBuilder<NovelMetaData>()
                .repository(novelMetaDataRepository)
                .methodName("save")
                .build();
        }catch (Exception ex){
            log.error("NovelMetaData 저장중 에러 발생, 에러내용 ={}",ex.getMessage());
            throw ex;
        }
    }

}