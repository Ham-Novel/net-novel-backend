package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소설 업데이트 요청 DTO")

public class NovelUpdateDto {

    private Long novelId;

    @Schema(description = "소설 작가 ID", example = "125")
    private String accessorProviderId;

    @Size(max = 30,message = "소설 제목은 30자 이하로 작성해주세요!")
    @Schema(description = "소설 제목", example = "마음의 소리", maxLength = 30)
    private String title;

    @Size(max = 300,message = "소설 상세설명은 300자 이하로 작성해주세요!")
    private String description;

    private List<String> tagNames;

    @Schema(description = "소설의 연재 상태", example = "ONGOING", allowableValues = {"ONGOING", "COMPLETED", "PAUSED"})
    private NovelType type;
}
