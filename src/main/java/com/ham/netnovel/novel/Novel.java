package com.ham.netnovel.novel;

//
//import com.ham.netnovel.author.Author;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    //제목
    @Column(nullable = false) //null 불가능
    private String title;

    //작품 설명
    @Column(nullable = false)
    private String description;

    //연재 상태 Enum 사용
    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    //작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private String authorId;

    @OneToMany(mappedBy = "novel")
    private List<Episode> episodes = new ArrayList<>();

}
