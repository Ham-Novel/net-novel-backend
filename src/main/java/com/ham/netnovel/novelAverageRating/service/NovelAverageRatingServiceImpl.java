package com.ham.netnovel.novelAverageRating.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelAverageRating.NovelAverageRating;
import com.ham.netnovel.novelAverageRating.NovelAverageRatingRepository;
import com.ham.netnovel.novelRating.dto.NovelRatingInfoDto;
import com.ham.netnovel.novelRating.service.NovelRatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
@Slf4j
public class NovelAverageRatingServiceImpl implements NovelAverageRatingService {

    private final NovelAverageRatingRepository novelAverageRatingRepository;

    private final NovelRatingService novelRatingService;

    private final NovelService novelService;

    public NovelAverageRatingServiceImpl(NovelAverageRatingRepository novelAverageRatingRepository, NovelRatingService novelRatingService, NovelService novelService) {
        this.novelAverageRatingRepository = novelAverageRatingRepository;
        this.novelRatingService = novelRatingService;
        this.novelService = novelService;
    }


    @Override
    @Transactional
    public void updateNovelAverageRating(Long novelId) {

        //novel 엔티티 조회, 없으면 예외로 던짐
        Novel novel = novelService.getNovelEntity(novelId)
                .orElseThrow(() -> new NoSuchElementException("updateNovelAverageRating 에러, Novel 정보가 없습니다, novelId = " + novelId));


        //소설에 등록된 별점 엔티티들을 받아와 DTO로 변환하여 List 객체에 담음
        List<NovelRatingInfoDto> novelRatingList = novelRatingService.getNovelRatingList(novelId);
        //등록된 별점의 평균을 계산하여 객체에 저장
        OptionalDouble average = novelRatingList.stream()
                .mapToDouble(NovelRatingInfoDto::getRating)
                .average();

        // 평균값이 존재하지 않거나, 등록된 별점이 없으면 메서드 종료
        if (average.isEmpty() || novelRatingList.isEmpty()) {
            log.info("등록된 Novel 별점이 없습니다. updateNovelAverageRating 메서드 종료");
            return;
        }

        //Novel 에 등록된 별점 수를 객체에 저장
        int listSize = novelRatingList.size();
        //평균 별점 소수점 셋째자리에서 반올림하여 객체에 저장
        BigDecimal averageValue = BigDecimal.valueOf(average.getAsDouble()).setScale(2, RoundingMode.HALF_UP);
        try {
            /*
        novel 평균 별점 엔티티가 DB에 저장되어 있는지 조회
        엔티티가 있을경우 내용 업데이트
        없을경우 엔티티 새로 만들어 DB에 저장
         */
            Optional<NovelAverageRating> optional = novelAverageRatingRepository.findByNovelId(novelId);
            if (optional.isEmpty()) {
                //새로운 엔티티 만들어 DB에 저장
                createNovelAverageRating(novel, averageValue, listSize);
                log.info("NovelAverageRating 생성 완료, novelId =" + novelId);
            } else {
                //조회된 엔티티 Optional 벗김
                NovelAverageRating novelAverageRating = optional.get();
                //엔티티 업데이트 후 DB에 저장
                updateNovelAverageRating(novelAverageRating, averageValue, listSize);
                log.info("NovelAverageRating 갱신 완료, novelId =" + novelId);
            }
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("updateNovelAverageRating 메서드 에러 발생" + ex.getMessage());
        }


    }

    @Override
    @Transactional
    public void updateAverageRatingForAllRatedNovels() {
        try {
            //별점이 등록되어있는 Novel 의 FK 값을 List로 받아옴
            List<Long> ratedNovelIds = novelService.getRatedNovelIds();
            // 별점이 등록되어 있는 Novel 의 평균 별점 업데이트
            for (Long ratedNovelId : ratedNovelIds) {
                updateNovelAverageRating(ratedNovelId);
            }
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("updateAverageRatingForAllRatedNovels 메서드 에러 발생" + ex.getMessage());
        }

    }

    @Override
    @Transactional
    public void createNovelAverageRating(Novel novel, BigDecimal averageValue, int listSize) {

        try {
            NovelAverageRating newNovelAverageRating = NovelAverageRating.builder()
                    .averageRating(averageValue)
                    .novel(novel)
                    .ratingCount(listSize)
                    .build();
            //DB에 엔티티 저장
            novelAverageRatingRepository.save(newNovelAverageRating);


        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovelAverageRating 메서드 에러 발생" + ex.getMessage());
        }
        //새로운 엔티티 생성

    }

    @Override
    @Transactional
    public void updateNovelAverageRating(NovelAverageRating novelAverageRating,
                                         BigDecimal averageValue,
                                         int listSize) {

        try {
            //엔티티 내용 변경
            novelAverageRating.updateNovelAverageRating(averageValue, listSize);
            //DB에 엔티티 저장
            novelAverageRatingRepository.save(novelAverageRating);

        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("updateNovelAverageRating 메서드 에러 발생" + ex.getMessage());
        }



    }


}
