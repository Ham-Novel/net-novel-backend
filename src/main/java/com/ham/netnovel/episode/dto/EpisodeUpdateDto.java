package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class EpisodeUpdateDto {

    private Long episodeId;

    @NotNull
    private String title;

    @NotNull
    private String content;
}
