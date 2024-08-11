package com.ham.netnovel.favoriteNovel.dto;

import com.ham.netnovel.tag.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteNovelInfoDto {
    @NotNull
    private Long novelId;

    @NotBlank
    private String novelTitle;

    @NotBlank
    private String authorName;

    @NotNull
    private List<Tag> tags;
}
