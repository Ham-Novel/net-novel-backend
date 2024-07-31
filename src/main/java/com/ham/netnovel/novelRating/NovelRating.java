package com.ham.netnovel.novelRating;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class NovelRating {

    @EmbeddedId
    private NovelRatingId id;

    //별점
    @Range(min = 1, max = 10, message = "별점은 1~10점만 입력 가능합니다.")
    private Integer rating;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("novelId")
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @Builder
    public NovelRating(NovelRatingId id, Integer rating, Member member, Novel novel) {
        this.id = id;
        this.rating = rating;
        this.member = member;
        this.novel = novel;
    }

    public void updateRating(int rating){
        this.rating = rating;


    }





}
