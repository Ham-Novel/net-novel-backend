package com.ham.netnovel.novel.dto;

import com.ham.netnovel.tag.dto.TagDataDto;
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
public class NovelListDto {//랭킹 등 리스트로 소설정보를 전달시 사용하는 DTO

    @NotNull
    private Long id; //소설 id

    @NotBlank
    @Size(max = 30)
    private String title; //소설 제목

    @NotNull
    private String authorName; //작가 닉네임

    @NotBlank
    private String desc;

    @NotNull
    private String providerId;//작가 소셜로그인 ID

    @NotNull
    private List<TagDataDto> tags; //작품 태그


    @NotNull
    private Integer totalFavorites; //총좋아요수


    private Long totalView;    //총조회수

    private LocalDateTime latestUpdateAt;//최근업데이트시간


    private String thumbnailUrl;//섬네일 URL




}
