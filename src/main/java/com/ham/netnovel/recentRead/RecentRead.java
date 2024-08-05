package com.ham.netnovel.recentRead;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class RecentRead {

    @EmbeddedId
    private RecentReadId id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @MapsId("memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    @MapsId("novelId")
    private Novel novel;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;



    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Builder
    public RecentRead(RecentReadId id, Member member, Novel novel, Episode episode) {
        this.id = id;
        this.member = member;
        this.novel = novel;
        this.episode = episode;
    }

    //최근 본 에피소드 업데이트
    public void updateEpisodeInfo(Episode episode){
      this.episode = episode;

    };

}
