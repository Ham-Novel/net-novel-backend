package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class NovelFavoriteDto {


    //소설PK
    private Long novelId;

    //소설제목
    private String title;

    private NovelStatus status;

    //작가이름 작가 엔티티 생성후 사용
    private String authorName;

    //작가 provider ID 작가 엔티티 생성후 사용
    private String authorProviderId;



}
