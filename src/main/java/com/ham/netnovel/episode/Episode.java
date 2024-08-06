package com.ham.netnovel.episode;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.coinUseHistory.CoinUseHistory;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    //에피소드 번호
    @Column(nullable = false)
    private Integer chapter;

    //에피소드 제목
    @Column(nullable = false)
    private String title;

    //에피소드 내용, 대용량이므로 @Lob 어노테이션으로 관리
    @Lob
    @Column(nullable = false)
    private String content;

    //에피소드 조회수
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer view;

    //생성일자
    @CreationTimestamp
    private LocalDateTime createdAt;

    //수정일자
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //활성, 삭제 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EpisodeStatus status = EpisodeStatus.ACTIVE;

    //Novel 테이블 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    //EpisodeCostPolicy 테이블 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_cost_policy_id")
    private CoinCostPolicy costPolicy;

    //junction table 연결, 댓글
    @OneToMany(mappedBy = "episode")
    private List<Comment> comments = new ArrayList<>();

    //junction table 연결, 코인 사용 기록
    @OneToMany(mappedBy = "episode")
    private List<CoinUseHistory> coinUseHistories = new ArrayList<>();

    @Builder
    public Episode(Integer chapter, String title, String content, Novel novel, CoinCostPolicy costPolicy) {
        this.chapter = chapter;
        this.title = title;
        this.content = content;
        this.novel = novel;
        this.view = 0;
        this.costPolicy = costPolicy;
    }

    public void updateEpisode(String title, String content, CoinCostPolicy costPolicy) {
        this.title = title;
        this.content = content;
        this.costPolicy = costPolicy;
    }

    public void changeStatus(EpisodeStatus status) {
        this.status = status;
    }
}
