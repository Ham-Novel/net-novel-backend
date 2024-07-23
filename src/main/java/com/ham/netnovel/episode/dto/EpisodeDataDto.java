package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;

public class EpisodeDataDto {

    private Long episodeId;

    @NotNull
    private Integer episodeNumber;

    @NotNull
    private String title;

    @NotNull
    private String content;

//    @NotNull
//    private Integer coinCost;

    private Integer view;
}
