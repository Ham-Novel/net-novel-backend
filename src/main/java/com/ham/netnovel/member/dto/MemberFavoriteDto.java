package com.ham.netnovel.member.dto;

import com.ham.netnovel.novel.data.NovelType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
public class MemberFavoriteDto {


    //소설PK
    @NotNull
    private Long novelId;

    //소설제목
    @NotNull
    private String title;

    @NotNull
    private NovelType status;

    @NotNull
    private Integer episodeCount; //에피소드 총 화수

    @NotNull
    private Integer views; //조회수

    @NotNull
    private Integer favoriteCount; //선호작 수
}
