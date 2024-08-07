package com.ham.netnovel.tag;


import com.ham.netnovel.novelTag.NovelTag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagStatus status;

    @OneToMany(mappedBy = "tag")
    private List<NovelTag> novelTags= new ArrayList<>();

    @Builder
    public Tag(String name, TagStatus status) {
        this.name = name;
        this.status = status;
    }

    public void changeStatus(TagStatus status) {
        this.status = status;
    }
}
