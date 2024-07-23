package com.ham.netnovel.novel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class NovelDeleteDto {
    @NotNull
    private Long novelId;

    private String accessorProviderId;
}
