package com.ham.netnovel.tag;


import com.ham.netnovel.novelTag.NovelTag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "tag_id")
    private List<NovelTag> novelTags= new ArrayList<>();


}
