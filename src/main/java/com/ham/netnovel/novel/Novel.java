package com.ham.netnovel.novel;


import com.ham.netnovel.author.Author;
import com.ham.netnovel.episode.Episode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    //제목
    @Column(nullable = false)//null일수 없음
    private String title;

    //작품 설명
    @Column(nullable = false)
    private String description;

    //연재 상태 enum으로 관리
    @Enumerated(EnumType.STRING)
    private NovelStatus status;


    //작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;


    @OneToMany(mappedBy = "novel")
    private List<Episode> episodes= new ArrayList<>();



}
