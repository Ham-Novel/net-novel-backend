package com.ham.netnovel.novelAverageRating;

import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
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
public class NovelAverageRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    //평균별점
    private BigDecimal averageRating;

    //별점갯수
    private int ratingCount;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Builder
    public NovelAverageRating(Novel novel, BigDecimal averageRating, int ratingCount) {
        this.novel = novel;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }


    //엔티티 업데이트 메서드, 평균 별점과 등록된 별점 엔티티 수 업데이트
    public void updateNovelAverageRating(BigDecimal averageRating, int ratingCount){
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;

    }

}
