package com.ham.netnovel.coinCostPolicy;

import com.ham.netnovel.episode.Episode;
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

    @OneToMany(mappedBy = "coinCostPolicy")
    private List<Episode> episodes;

    //생성 메서드
    @Builder
    public CoinCostPolicy(String name, Integer coinCost) {
        this.name = name;
        this.coinCost = coinCost;
    }

    //업데이트 메서드
    public void update(String name, Integer coinCost) {
        this.name = name;
        this.coinCost = coinCost;
    }
}
