package com.ham.netnovel.novelGenere;


import com.ham.netnovel.genre.Genre;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;

@Entity

public class NovelGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;



    @ManyToOne
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @ManyToOne
    @JoinColumn(name = "genere_id")
    private Genre genre;

}
