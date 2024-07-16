package com.ham.netnovel.genre;


import com.ham.netnovel.novelGenere.NovelGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    @NotBlank
    @Column(nullable = false)
    private String name;

    //junction table 연결, 소설 장르
    @OneToMany(mappedBy = "genre")
    private List<NovelGenre> novelGenres = new ArrayList<>();

}
