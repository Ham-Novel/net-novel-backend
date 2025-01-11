package com.ham.netnovel.episode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Builder
@Setter
@Getter
@ToString
@Schema(description = "에피소드 상세 정보를 나타내는 DTO")

public class EpisodeDetailDto {
    @NotNull
    @Schema(description = "에피소드 ID", example = "1")

    private Long episodeId;

    //제목
    @NotNull
    @Schema(description = "에피소드 제목", example = "첫 번째 이야기")
    private String title;

    //내용
    @NotNull
    @Schema(description = "에피소드 내용", example = "이야기의 상세 내용이 여기에 포함됩니다.")
    private String content;
}
