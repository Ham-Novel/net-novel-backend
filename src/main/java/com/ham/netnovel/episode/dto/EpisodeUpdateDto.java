package com.ham.netnovel.episode.dto;

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
public class EpisodeUpdateDto {

    private Long episodeId;

    @NotNull
    @Size(min = 1, max = 100,message = "제목은 최대 100 자까지 작성 가능합니다!")//최대 100자까지 제한
    private String title;

    @NotNull
    @Size(min = 1, max = 10000,message = "에피소드는 최대 10000 자까지 작성 가능합니다!")//최대 10000자까지 제한
    private String content;

    @NotNull
    private Long costPolicyId;

    private String providerId;//에피소드 업데이트 요청자 정보
}
