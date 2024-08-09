package com.ham.netnovel.novelRanking.dto;

import com.ham.netnovel.novel.Novel;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NovelRankingUpdateDto {

    private Novel novel;

    private Integer ranking;



    private Long totalViews;


}
