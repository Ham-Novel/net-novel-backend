package com.ham.netnovel.novel;

import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novel.dto.NovelResponseDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novelAverageRating.NovelAverageRating;
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
    @Column(nullable = false, length = 30) //null 불가능
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

    //작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @OneToMany(mappedBy = "novel")
    private List<Episode> episodes = new ArrayList<>();

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
    public void updateNovel(NovelUpdateDto updateDto){
        this.title = updateDto.getTitle();
        this.description = updateDto.getDescription();
        this.type = updateDto.getType();
    }

    public NovelResponseDto parseResponseDto() {
        return NovelResponseDto.builder()
                .novelId(this.id)
                .title(this.title)
                .description(this.description)
                .authorName(this.author.getNickName())
                .type(this.type)
                .view(episodes.stream().mapToInt(epi->epi.getView()).sum())
                .episodeAmount(episodes.size())
                .build();
    }
}
