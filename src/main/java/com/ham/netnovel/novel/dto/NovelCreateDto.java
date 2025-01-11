package com.ham.netnovel.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소설 생성 DTO")

public class NovelCreateDto {
    @NotBlank
    @Size(max = 30,message = "소설 제목은 30자 이하로 작성해주세요!")
    @Schema(description = "소설 제목", example = "마음의 소리", maxLength = 30)
    private String title;

    @Size(max = 300)
    @Schema(description = "작품 소개 (최대 300자)", example = "이 작품은 판타지 세계에서 펼쳐지는 흥미로운 모험을 다룹니다.")
    private String description;

    @Schema(description = "소설 작가 ID", example = "125")
    private String accessorProviderId; //실행자 유저 ID

    @Schema(description = "작품 태그 리스트")
    private List<String> tagNames;//작가가 선택한 태그의 이름들


}
