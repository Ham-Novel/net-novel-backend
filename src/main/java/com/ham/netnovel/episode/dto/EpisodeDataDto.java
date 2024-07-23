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
public class EpisodeDataDto {

    private Long episodeId;

    @NotNull
    private Integer episodeNumber;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private Integer view;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
