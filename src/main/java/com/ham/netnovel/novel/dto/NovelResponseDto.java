package com.ham.netnovel.novel.dto;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.NovelStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class NovelResponseDto {

    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    private String authorName;

    private NovelStatus status;
}
