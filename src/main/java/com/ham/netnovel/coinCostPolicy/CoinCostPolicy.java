package com.ham.netnovel.coinCostPolicy;

import com.ham.netnovel.coinCostPolicy.data.PolicyDBStatus;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyDBStatus status = PolicyDBStatus.ACTIVE;

    @OneToMany(mappedBy = "coinCostPolicy")
    private List<Episode> episodes;

    //생성 메서드
    @Builder
    public CoinCostPolicy(String name, Integer coinCost, PolicyRange policyRange, Integer rangeValue) {
        this.name = name;
        this.coinCost = coinCost;
        this.policyRange = policyRange;
        this.rangeValue = rangeValue;
    }

    //업데이트 메서드
    public void update(String name, Integer coinCost, PolicyRange policyRange, Integer rangeValue) {
        this.name = name;
        this.coinCost = coinCost;
        this.policyRange = policyRange;
        this.rangeValue = rangeValue;
    }

    //삭제, 복구 처리 메서드
    public void changeStatus(PolicyDBStatus status) {
        this.status = status;
    }
}
