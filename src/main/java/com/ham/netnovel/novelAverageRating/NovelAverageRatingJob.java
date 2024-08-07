package com.ham.netnovel.novelAverageRating;

import com.ham.netnovel.novelAverageRating.service.NovelAverageRatingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NovelAverageRatingJob implements Job {



    private final NovelAverageRatingService novelAverageRatingService;
    @Autowired
    public NovelAverageRatingJob(NovelAverageRatingService novelAverageRatingService) {
        this.novelAverageRatingService = novelAverageRatingService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Novel 평균 별점 업데이트 시작");
        novelAverageRatingService.updateAverageRatingForAllRatedNovels();
    }
}
