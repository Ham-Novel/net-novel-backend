package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.tag.Tag;
import jakarta.validation.constraints.NotBlank;
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
public class NovelInfoDto {

    @NotNull
    private Long novelId; //소설 id

    @NotBlank
    @Size(max = 30)
    private String title; //소설 제목

    @NotBlank
    @Size(max = 300)
    private String description; //작품 소개

    private NovelType type; //소설 등급

    private String authorName; //작가 닉네임

    private Integer views; //조회수

    private Integer favoriteCount; //선호작 수

    private Integer episodeCount; //에피소드 총 화수

    private List<Tag> tags; //작품 태그

}
