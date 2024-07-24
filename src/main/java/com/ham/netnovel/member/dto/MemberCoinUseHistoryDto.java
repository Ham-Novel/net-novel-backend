package com.ham.netnovel.member.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class MemberCoinUseHistoryDto {

    //에피소드 번호
    //에피소드 이름
    //결제 날짜

    //소설 제목
    private String novelTitle;

    //에피소드 번호
    private Integer episodeNumber;

    //에피소드 제목
    private String episodeTitle;


    //사용 코인 갯수
    private Integer usedCoin;

    //결제 날짜
    private LocalDateTime createdAt;






}
