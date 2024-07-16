package com.ham.netnovel.episodeRating;


import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
public class EpisodeRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    //별점
    @Range(min=1, max=10,message = "별점은 1~10점만 입력 가능합니다.")
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;


}
