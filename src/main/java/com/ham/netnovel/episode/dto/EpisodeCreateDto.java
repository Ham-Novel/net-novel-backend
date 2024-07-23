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
public class EpisodeCreateDto {

    @NotNull
    private Long novelId;

    @NotNull
    private String title;

    @NotNull
    private String content;
}
