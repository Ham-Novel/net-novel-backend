package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    private String accessorPId;
}
