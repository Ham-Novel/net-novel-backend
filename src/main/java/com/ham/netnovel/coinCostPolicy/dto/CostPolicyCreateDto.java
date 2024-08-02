package com.ham.netnovel.coinCostPolicy.dto;

import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
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

    @NotNull
    private PolicyRange policyRange;

    private Integer rangeValue;
}
