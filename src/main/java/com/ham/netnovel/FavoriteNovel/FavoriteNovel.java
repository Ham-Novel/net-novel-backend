package com.ham.netnovel.favoriteNovel;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FavoriteNovel {

    //member_id와 novel_id로 구성된 composite PK
    @EmbeddedId
    private FavoriteNovelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("novelId")
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @Builder
    public FavoriteNovel(FavoriteNovelId id, Member member, Novel novel) {
        this.id = id;
        this.member = member;
        this.novel = novel;
    }
}
