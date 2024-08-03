package com.ham.netnovel.member;


import com.ham.netnovel.coinUseHistory.CoinUseHistory;
import com.ham.netnovel.coinChargeHistory.CoinChargeHistory;
import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.favoriteNovel.FavoriteNovel;
import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.novelRating.NovelRating;
import com.ham.netnovel.recentRead.RecentRead;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto_increment 자동생성
    private Long id;


    //  유저 email, naver는 설정값에 따라 도메인이 naver가 아닐수도 있음
    @Column(unique = true)
    private String email;


    //유저 정보 제공자,(naver,kakao등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider;


    //유저 정보 제공자에서 관리하는 유저 Id값
    @Column(unique = true)
    private String providerId;

    //유저 역할, ADMIN, AUTHOR, READER 3종류
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    //유저 닉네임
    private String nickName;

    //성별
    @Enumerated(EnumType.STRING)
    private Gender gender;


    //보유한 코인의 갯수
    private Integer coinCount;
    //junction table 연결, 관심 소설
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<FavoriteNovel> favoriteNovels = new ArrayList<>();


    //1:N 연결,,별 점준 에피소드
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<NovelRating> novelRatings = new ArrayList<>();

    //junction table 연결, 코인 충전 기록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<CoinChargeHistory> coinChargeHistories = new ArrayList<>();

    //junction table 연결, 코인 사용 기록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<CoinUseHistory> coinUseHistories = new ArrayList<>();


    //1:N 연결, 에피소드 댓글
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //junction table 연결, 최근읽은 소설
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecentRead> recentReads = new ArrayList<>();

    @Builder
    public Member(String email, OAuthProvider provider, String providerId, MemberRole role, String nickName, Gender gender, Integer coinCount) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.nickName = nickName;
        this.gender = gender;
    }

    public void changeNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Member 엔티티 코인 수를 감수시키는 메서드
     * 파라미터 체크는 메서드를 사용하는 비즈니스 로직에서 진행
     * @param coinCount
     */
    public void deductMemberCoins(int coinCount) {
        Integer totalCoin = this.getCoinCount();
        this.coinCount = totalCoin - coinCount;
    }

    /**
     * Member 엔티티 코인 수를 증가 시키는 메서드
     * 파라미터 체크는 메서드를 사용하는 비즈니스 로직에서 진행
     * @param coinCount 증가시키려는 코인 수
     */
    public void increaseMemberCoins(int coinCount){
        Integer totalCoin = this.getCoinCount();
        this.coinCount = totalCoin + coinCount;
    }

}
