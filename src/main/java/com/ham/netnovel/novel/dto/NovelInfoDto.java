package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.tag.dto.TagDataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소설 상세 정보를 제공하는 DTO")
public class NovelInfoDto {

    @NotNull
    @Schema(description = "소설 ID", example = "12345")
    private Long id; //소설 id

    @NotBlank
    @Size(max = 30)
    @Schema(description = "소설 제목 (최대 30자)", example = "모험의 시작")
    private String title; //소설 제목

    @NotBlank
    @Size(max = 300)
    @Schema(description = "작품 소개 (최대 300자)", example = "이 작품은 판타지 세계에서 펼쳐지는 흥미로운 모험을 다룹니다.")
    private String desc; //작품 소개

    @NotNull
    @Schema(description = "작가 닉네임", example = "김춘배")
    private String authorName; //작가 닉네임

    @NotNull
    @Schema(description = "총 조회수", example = "123456")
    private Integer views; //조회수

    @NotNull
    @Schema(description = "평균 별점", example = "4.7")
    private BigDecimal averageRating; //평균 별점

    @NotNull
    @Schema(description = "선호작 등록 수", example = "1500")
    private Integer favoriteCount; //선호작 수

    @NotNull
    @Schema(description = "작품 태그 리스트")
    private List<TagDataDto> tags; //작품 태그

    @NotNull
    @Schema(description = "소설 연재상태", example = "ONGOING")

    private NovelType type; //소설 연재상태

    @NotNull
    @Schema(description = "총 에피소드 수", example = "120")
    private Integer episodeCount; //에피소드 총 화수

    @NotNull
    @Schema(description = "소설 섬네일 AWS URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;//섬네일 AWS URL

}
