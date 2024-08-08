package com.ham.netnovel.novelTag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelTagCreateDto {
    @NotNull
    private Long novelId;
    @NotBlank
    private String tagName;

}
