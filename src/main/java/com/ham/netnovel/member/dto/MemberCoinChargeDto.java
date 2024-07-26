package com.ham.netnovel.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberCoinChargeDto {


    private String providerId;


    @NotNull
    private Integer coinAmount;

    @NotNull
    private LocalDateTime createdAt;


    @NotNull
    private BigDecimal payment;

}
