package com.ham.netnovel.episode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@Schema(description = "에피소드 수정시 정보를 나타내는 DTO")

public class EpisodeUpdateDto {

    private Long episodeId;

    @NotNull
    @Size(min = 1, max = 100,message = "제목은 최대 100 자까지 작성 가능합니다!")//최대 100자까지 제한
    @Schema(description = "에피소드 제목", example = "첫 번째 이야기")
    private String title;

    @NotNull
    @Size(min = 1, max = 10000,message = "에피소드는 최대 10000 자까지 작성 가능합니다!")//최대 10000자까지 제한
    @Schema(description = "에피소드 내용", example = "이야기의 상세 내용이 여기에 포함됩니다.")
    private String content;

    @NotNull
    @Schema(description = "에피소드 가격정책 ID", example = "1")
    private Long costPolicyId;

    @Schema(description = "작가의 Provider ID", example = "user123")
    private String providerId;//에피소드 업데이트 요청자 정보
}
