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
public class NovelUpdateDto {

    @NotNull
    private Long novelId;

    @NotBlank
    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private NovelStatus status;

//    private String authorProviderId;

    private String accessorProviderId;

    public boolean isSameContent(Novel novel) {
        return this.title.equals(novel.getTitle())
                && this.description.equals(novel.getDescription())
                && this.status.equals(novel.getStatus());
    }
}
