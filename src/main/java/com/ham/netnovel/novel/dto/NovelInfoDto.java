package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.tag.Tag;
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
public class NovelInfoDto {

    @NotNull
    private Long novelId; //소설 id

    @NotBlank
    @Size(max = 30)
    private String title; //소설 제목

    @NotBlank
    @Size(max = 300)
    private String description; //작품 소개

    @NotNull
    private String authorName; //작가 닉네임

    @NotNull
    private Integer views; //조회수

    @NotNull
    private BigDecimal averageRating; //평균 별점

    @NotNull
    private Integer favoriteCount; //선호작 수

    @NotNull
    private NovelType type; //소설 등급

    @NotNull
    private Integer episodeCount; //에피소드 총 화수

//    @NotNull
//    private List<Tag> tags; //작품 태그

}
