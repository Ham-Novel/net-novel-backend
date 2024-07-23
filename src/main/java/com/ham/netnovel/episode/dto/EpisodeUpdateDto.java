package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;

public class EpisodeUpdateDto {

    private Long episodeId;

    @NotNull
    private Integer episodeNumber;

    @NotNull
    private String title;

    @NotNull
    private String content;
}
