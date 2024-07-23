package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;

public class EpisodeCreateDto {

    @NotNull
    private String title;

    @NotNull
    private String content;
}
