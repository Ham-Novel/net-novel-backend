package com.ham.netnovel.settlement;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer coinCount;

    //정산수익(단위 원)
    @NotNull
    @Column(nullable = false)

    private Integer revenue;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;//정산신청날짜

    private LocalDateTime completionDate;  // 정산 완료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;


    @Builder
    public Settlement(Integer coinCount, Integer revenue, SettlementStatus status, Member member, Novel novel) {
        this.coinCount = coinCount;
        this.revenue = revenue;
        this.status = status;
        this.member = member;
        this.novel = novel;
    }
}
