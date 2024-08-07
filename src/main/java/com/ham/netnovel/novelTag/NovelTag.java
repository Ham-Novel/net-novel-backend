package com.ham.netnovel.novelTag;


import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.tag.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class NovelTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;


}
