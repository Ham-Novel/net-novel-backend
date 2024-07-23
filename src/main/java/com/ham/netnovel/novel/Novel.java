package com.ham.netnovel.novel;

//
//import com.ham.netnovel.author.Author;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    //제목
    @Column(nullable = false, length = 30) //null 불가능
    private String title;

    //작품 설명
    @Column(nullable = false, length = 300)
    private String description;

    //연재 상태 Enum 사용
    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    //작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @OneToMany(mappedBy = "novel")
    private List<Episode> episodes = new ArrayList<>();

    @Builder
    public Novel(String title, String description, NovelStatus status, Member author) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.author = author;
    }
}
