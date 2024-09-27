package com.ham.netnovel.novel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelDeleteDto {
    @NotNull
    private Long novelId;

    private String accessorProviderId;
}
