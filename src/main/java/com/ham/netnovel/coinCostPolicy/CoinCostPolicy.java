package com.ham.netnovel.coinCostPolicy;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class CoinCostPolicy {

    @Id //인조키
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment 자동 생성
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private Integer coinCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyRange policyRange;

    @Column(nullable = false)
    private Integer rangeValue;

    @OneToMany(mappedBy = "episode")
    private List<Episode> episodes;
}
