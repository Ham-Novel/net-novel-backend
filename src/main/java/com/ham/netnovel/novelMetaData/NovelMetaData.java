package com.ham.netnovel.novelMetaData;

import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class NovelMetaData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    private LocalDateTime latestEpisodeAt;

    Long totalViews;

    int totalFavorites;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    Novel novel;

//    @Builder
//    public NovelMetaData(Long totalViews, int totalFavorites, Novel novel) {
//        this.totalViews = totalViews;
//        this.totalFavorites = totalFavorites;
//        this.novel = novel;
//    }
    @Builder
    public NovelMetaData(LocalDateTime latestEpisodeAt, Long totalViews, int totalFavorites, Novel novel) {
        this.latestEpisodeAt = latestEpisodeAt;
        this.totalViews = totalViews;
        this.totalFavorites = totalFavorites;
        this.novel = novel;
    }

    public void updateTotalViews(Long totalViews){
        this.totalViews = totalViews;

    }
    public void updateTotalFavorites(Integer totalFavorites){
        this.totalFavorites = totalFavorites;

    }

    public void updatedLatestEpisodeAt(LocalDateTime latestEpisodeAt){
        this.latestEpisodeAt = latestEpisodeAt;

    }
}
