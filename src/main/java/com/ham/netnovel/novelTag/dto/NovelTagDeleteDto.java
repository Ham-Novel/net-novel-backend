package com.ham.netnovel.novelTag.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelTagDeleteDto {
    @NotNull
    private Long novelId;

    @NotNull
    private Long tagId;

    private String tagName;
}
