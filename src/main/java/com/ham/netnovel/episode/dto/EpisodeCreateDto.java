package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeCreateDto {

    @NotNull
    private Long novelId;

    @NotNull
    private String title;

    @NotNull
    private String content;
}
