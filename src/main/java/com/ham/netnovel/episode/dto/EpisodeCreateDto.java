package com.ham.netnovel.episode.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeCreateDto {

    private Long novelId;

    @NotNull
    @Size(min = 1, max = 100,message = "제목은 최대 100 자까지 작성 가능합니다!")//최대 100자까지 제한
    private String title;

    @NotNull
    @Size(min = 1, max = 10000,message = "에피소드는 최대 10000 자까지 작성 가능합니다!")//최대 10000자까지 제한
    private String content;//내용

    @NotNull
    private Long costPolicyId;


    private String providerId;//에피소드 생성 요청자 정보
}
