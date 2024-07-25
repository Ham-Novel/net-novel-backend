package com.ham.netnovel.coinUseHistory.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class CoinUseCreateDto {

    //에피소드 정보
    @NotNull
    private Long episodeId;

    //유저 정보
    private String providerId;





}
