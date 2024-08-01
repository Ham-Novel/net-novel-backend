package com.ham.netnovel.novelRating.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class NovelRatingInfoDto {

    //점수
    private Integer rating;

    //수정날짜
    private LocalDateTime updatedAt;


}
