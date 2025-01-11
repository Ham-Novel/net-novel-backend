package com.ham.netnovel.novel.dto;

import com.ham.netnovel.tag.dto.TagDataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "소설 정보를 전달하는 DTO, 주로 랭킹 및 리스트에 사용")
public class NovelListDto {//랭킹 등 리스트로 소설정보를 전달시 사용하는 DTO

    @NotNull
    @Schema(description = "소설 ID", example = "12345")
    private Long id; //소설 id

    @NotBlank
    @Size(max = 30)
    @Schema(description = "소설 제목 (최대 30자)", example = "환상의 모험")
    private String title; //소설 제목

    @NotNull
    @Schema(description = "작가 닉네임", example = "김춘배")
    private String authorName; //작가 닉네임

    @NotNull
    @Schema(description = "작가 소셜 로그인 ID", example = "provider123")
    private String providerId;//작가 소셜로그인 ID

    @NotNull
    @Schema(description = "작품 태그 리스트")
    private List<TagDataDto> tags; //작품 태그


    @NotNull
    @Schema(description = "총 좋아요 수", example = "245")
    private Integer totalFavorites; //총좋아요수

    @Schema(description = "총 조회수", example = "15000")
    private Long totalView;    //총조회수

    @Schema(description = "최근 업데이트 시간", example = "2024-12-26T14:00:00")
    private LocalDateTime latestUpdateAt;//최근업데이트시간

    @Schema(description = "소설 섬네일 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;//섬네일 URL

    @Schema(description = "소설 설명", example = "이 소설은 판타지 세계에서 벌어지는 모험을 다룹니다.")
    private String description;//소설 설명




}
