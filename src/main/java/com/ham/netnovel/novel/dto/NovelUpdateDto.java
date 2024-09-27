package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelUpdateDto {

    private Long novelId;

    private String accessorProviderId;

    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private List<String> tagNames;

    private NovelType type;
}
