package com.ham.netnovel.novelTag;


import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.tag.Tag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class NovelTag {

    //tag_id와 novel_id로 구성된 composite PK
    @EmbeddedId
    private NovelTagId id;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("novelId")
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @Builder
    public NovelTag(NovelTagId id, Tag tag, Novel novel) {
        this.id = id;
        this.tag = tag;
        this.novel = novel;
    }


}
