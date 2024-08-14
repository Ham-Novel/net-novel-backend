package com.ham.netnovel.episode.job;

import com.ham.netnovel.episode.service.EpisodeManagementService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EpisodeViewCountUpdateJob implements Job {

private final EpisodeManagementService episodeManagementService;

    public EpisodeViewCountUpdateJob(EpisodeManagementService episodeManagementService) {
        this.episodeManagementService = episodeManagementService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("에피소드 조회수 업데이트 시작");
        episodeManagementService.updateEpisodeViewCountFromRedis();
        log.info("에피소드 조회수 업데이트 완료");

    }
}
