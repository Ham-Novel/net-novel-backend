package com.ham.netnovel.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class NovelCreateDto {
    @NotBlank
    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    //작가 provider ID
    private String authorProviderId;
}
