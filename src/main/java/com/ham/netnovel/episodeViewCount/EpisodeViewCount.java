package com.ham.netnovel.episodeViewCount;

import com.ham.netnovel.episode.Episode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "episode_view_count",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "episode_id",
                "view_date"        }))//에피소드 id 와 날짜로 composite key 생성(유니크 조건)
//에피소드 조회수를 count하기 위한 엔티티
public class EpisodeViewCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    private LocalDate viewDate;

    private Integer viewCount;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public EpisodeViewCount(Episode episode, LocalDate viewDate, Integer viewCount) {
        this.episode = episode;
        this.viewDate = viewDate;
        this.viewCount = viewCount;
    }

    public EpisodeViewCount increaseViewCount(Integer viewCount) {
        if (this.viewCount==null){
            this.viewCount = 1;
        }
        else {
            this.viewCount = this.getViewCount() + viewCount;
        }

        return this;
    }


}
