package com.ham.netnovel.member.dto;


import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "유저가 최근 읽은 작품에 대한 정보 DTO")
public class MemberRecentReadDto {

    @Schema(description = "소설 ID", example = "123")
    private Long id;

    @Schema(description = "소설 제목", example = "마법사의 후예")
    private String title;

    @Schema(description = "소설의 연재 상태", example = "ONGOING")
    private NovelType novelType;

    @Schema(description = "작가 이름", example = "김춘배")
    private String authorName;

    @Schema(description = "최근 읽은 에피소드 제목", example = "1화 - 시작의 장")
    private String episodeTitle;

    @Schema(description = "최근 읽은 에피소드 ID", example = "456")
    private Long episodeId;

    @Schema(description = "최근 업데이트 시간", example = "2024-12-25T10:15:30")
    private LocalDateTime updatedAt;

    @Schema(description = "소설의 섬네일 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;//섬네일 URL




}
