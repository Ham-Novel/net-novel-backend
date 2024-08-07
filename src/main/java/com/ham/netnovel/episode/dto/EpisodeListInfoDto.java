package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.Min;
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
public class EpisodeListInfoDto {

    @NotNull
    private Integer chapterCount;

    @NotNull
    private LocalDateTime lastUpdatedAt;
}
