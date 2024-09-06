package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Builder
@Setter
@Getter
@ToString
public class EpisodePaymentDto {
    @NotNull
    private Long episodeId;

    @NotNull
    private String title;

    @NotNull
    private Integer coinCost;
}
