package com.ham.netnovel.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "유저의 코인 충전 기록 DTO")

public class MemberCoinChargeDto {


    @Schema(description = "유저의 Provider ID", example = "user123")
    private String providerId;


    @Schema(description = "충전된 코인의 양", example = "100")
    @NotNull
    private Integer coinAmount;

    @Schema(description = "충전된 날짜 및 시간", example = "2024-12-25T15:30:00")
    @NotNull
    private LocalDateTime createdAt;

    @Schema(description = "결제 금액", example = "100000")
    @NotNull
    private BigDecimal payment;

}
