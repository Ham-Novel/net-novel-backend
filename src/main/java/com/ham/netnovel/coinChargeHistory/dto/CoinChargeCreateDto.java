package com.ham.netnovel.coinChargeHistory.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinChargeCreateDto {

    private String providerId;

    //충전한 코인수
    //ToDo 적절한 코인수 설정 필요
    @Range(max = 1000, min = 1,message = "충전 코인수가 올바르지 않습니다.")
    private Integer coinAmount;

    @NotNull
    //지불금액 String으로 받아서 BigDecimal로 변환
    private String payment;


}
