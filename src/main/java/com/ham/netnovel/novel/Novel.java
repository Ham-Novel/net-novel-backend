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
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    @Column(nullable = false)
    private String authorId;

//    @OneToMany(mappedBy = "novel")
//    private List<Episode> episodes = new ArrayList<>();

}
