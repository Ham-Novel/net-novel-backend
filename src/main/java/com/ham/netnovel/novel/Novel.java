package com.ham.netnovel.novel;

import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.favoriteNovel.FavoriteNovel;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novelAverageRating.NovelAverageRating;
import com.ham.netnovel.novelTag.NovelTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;

    //제목
    @Column(unique = true, nullable = false, length = 30) //null 불가능
    private String title;

    //작품 설명
    @Column(nullable = false, length = 300)
    private String description;

    //연재 상태 Enum 사용
    @Enumerated(EnumType.STRING)
    private NovelType type;

    //삭제 처리 상태 Enum 사용
    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    //섬네일 파일명
    private String thumbnailFileName;

    //작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    //작품 에피소드들
    @OneToMany(mappedBy = "novel")
    private List<Episode> episodes = new ArrayList<>();

    //선호작 수
    @OneToMany(mappedBy = "novel")
    private List<FavoriteNovel> favorites;

    //태그 목록
    @OneToMany(mappedBy = "novel")
    private List<NovelTag> novelTags;

    //평균 별점
    @OneToOne(mappedBy = "novel")
    private NovelAverageRating novelAverageRating;

    @Builder
    public Novel(String title, String description, Member author, NovelType type, NovelStatus status) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.type = type;
        this.status = status;
    }

    //댓글 엔티티 상태 변경
    public  void changeStatus(NovelStatus status) {
        this.status = status;
    }

    //댓글 엔티티 내용 변경
    public void updateTitle(String title){
        this.title = title;
    }
    public void updateDesc(String description){
        this.description = description;
    }
    public void updateType(NovelType type){
        this.type = type;
    }

    //섬네일 파일명 변경
    public void updateThumbnailFileName(String thumbnailFileName){
        this.thumbnailFileName = thumbnailFileName;

    }

}
