package com.ham.netnovel.episodeViewCount;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewCountIncreaseDto {

    Long episodeId;

    Integer viewCount;

}
