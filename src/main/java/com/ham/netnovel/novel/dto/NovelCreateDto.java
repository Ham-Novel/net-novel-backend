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

    private String accessorProviderId; //실행자 유저 ID

    private List<String> tagNames;//작가가 선택한 태그의 이름들


}
