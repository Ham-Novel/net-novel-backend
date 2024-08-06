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
public class EpisodeDetailDto {
    @NotNull
    private Long episodeId;

    //제목
    @NotNull
    private String title;

    //내용
    @NotNull
    private String content;
}
