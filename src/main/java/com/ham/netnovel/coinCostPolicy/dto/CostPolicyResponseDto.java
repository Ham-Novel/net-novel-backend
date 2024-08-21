package com.ham.netnovel.coinCostPolicy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostPolicyResponseDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Integer coinCost;
}
