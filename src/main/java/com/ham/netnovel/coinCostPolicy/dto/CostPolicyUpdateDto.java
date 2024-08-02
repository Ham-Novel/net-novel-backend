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
public class CostPolicyUpdateDto {

    @NotNull
    private Long id;

    private String name;

    private Integer coinCost;

    private PolicyRange policyRange;

    private Integer rangeValue;
}
