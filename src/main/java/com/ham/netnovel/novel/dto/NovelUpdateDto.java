package com.ham.netnovel.novel.dto;

import com.ham.netnovel.novel.data.NovelType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelUpdateDto {

    @NotNull
    private Long novelId;

    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private NovelType type;

//    private String authorProviderId;

    private String accessorProviderId;
}
