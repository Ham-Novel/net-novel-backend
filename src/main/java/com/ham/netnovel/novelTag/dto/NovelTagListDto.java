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
public class NovelTagListDto {
    @NotNull
    private Long tagId;

    @NotBlank
    private String tagName;
}
