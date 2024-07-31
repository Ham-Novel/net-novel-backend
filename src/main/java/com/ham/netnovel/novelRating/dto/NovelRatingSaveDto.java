package com.ham.netnovel.novelRating.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class NovelRatingSaveDto {

    private Integer rating;

    private String providerId;

    private Long novelId;


}
