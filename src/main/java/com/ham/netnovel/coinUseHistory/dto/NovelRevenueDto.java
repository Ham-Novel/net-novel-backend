package com.ham.netnovel.coinUseHistory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NovelRevenueDto {

    @NotNull
    private Long novelId;

    @NotNull
    private String novelTitle;

    @NotNull
    private String providerId;//작가 정보

    @NotNull
    private Integer totalCoins;


    // 정산 기간을 명확히 하기 위한 필드, 정산시작일
    @NotNull
    private LocalDate settlementStartDate;
    // 정산 기간을 명확히 하기 위한 필드, 정산마지막일(정산신청일 전날)
    @NotNull
    private LocalDate settlementEndDate;


    public NovelRevenueDto(Long novelId, String novelTitle, Integer totalCoins) {
        this.novelId = novelId;
        this.novelTitle = novelTitle;
        this.totalCoins = totalCoins;
    }
}
