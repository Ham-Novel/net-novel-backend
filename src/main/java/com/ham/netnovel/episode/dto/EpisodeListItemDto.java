package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class EpisodeListItemDto {

    @NotNull
    private Long episodeId;

    @NotNull
    private Integer chapter;

    @NotNull
    private String title;

    @NotNull
    private Integer views;

    @NotNull
    private Integer letterCount;

    @NotNull
    private Integer commentCount;

    @NotNull
    private LocalDateTime uploadDate;
}
