package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.NovelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelDataDto {

    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private String authorName;

    private NovelStatus status;
}
