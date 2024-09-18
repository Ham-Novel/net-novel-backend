package com.ham.netnovel.novel.dto;

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
public class NovelCreateDto {
    @NotBlank
    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private List<String> tagNames; //작품 태그

    private String accessorProviderId; //실행자 유저 ID
}
