package com.ham.netnovel.episode;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.episode.dto.EpisodeDataDto;
import com.ham.netnovel.coinUseHistory.CoinUseHistory;
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
    private Integer episodeNumber;

    //에피소드 제목
    @Column(nullable = false)
    private String title;

    //에피소드 내용, 대용량이므로 @Lob 어노테이션으로 관리
    @Lob
    @Column(nullable = false)
    private String content;

    //ToDo 편당 결제 금액은 Novel 등급에 따라서 고정되어 있음. 굳이 Episode에 넣을 필요가 있을까?
    //조회를 위한 코인의 갯수
//    @Column(nullable = false)
//    private Integer coinCost;

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


    //Novel 테이블 연결
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @OneToMany(mappedBy = "episode")
    private List<Comment> comments = new ArrayList<>();


    //junction table 연결, 코인 사용 기록
    @OneToMany(mappedBy = "episode")
    private List<CoinUseHistory> coinUseHistories = new ArrayList<>();

    @Builder
    public Episode(Integer episodeNumber, String title, String content, Novel novel) {
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.content = content;
        this.novel = novel;
        this.view = 0;
    }

    public EpisodeDataDto parseDataDto() {
        return EpisodeDataDto.builder()
                .episodeId(this.id)
                .episodeNumber(this.episodeNumber)
                .title(this.title)
                .content(this.content)
                .view(this.view)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .commentAmount(comments.size())
                .build();
    }

    public void updateEpisode(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
