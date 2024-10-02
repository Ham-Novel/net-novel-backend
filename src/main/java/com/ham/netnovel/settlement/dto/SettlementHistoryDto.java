package com.ham.netnovel.settlement.dto;


import com.ham.netnovel.settlement.SettlementStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class SettlementHistoryDto {


    private Integer coinCount;

    //정산수익(단위 원)
    private Integer revenue;

    private SettlementStatus status;

    private String  createdAt;//정산신청날짜

    private String  completionDate;  // 정산 완료일

    private String novelTitle;

}
