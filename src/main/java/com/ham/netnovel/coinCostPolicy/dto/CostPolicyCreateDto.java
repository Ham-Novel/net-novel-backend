package com.ham.netnovel.coinCostPolicy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostPolicyCreateDto {

    @NotNull
    private String name;

    @NotNull
    private Integer coinCost;
}
