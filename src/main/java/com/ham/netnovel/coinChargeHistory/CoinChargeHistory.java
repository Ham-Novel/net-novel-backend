package com.ham.netnovel.coinChargeHistory;


import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CoinChargeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


   //충전한 코인수
    @NotNull
    private Integer amount;

    @NotNull
    @Column(precision = 10, scale = 2)
    //지불금액
    private BigDecimal payment;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;


    @Builder
    public CoinChargeHistory(Integer amount, BigDecimal payment, Member member) {
        this.amount = amount;
        this.payment = payment;
        this.member = member;
    }

}
