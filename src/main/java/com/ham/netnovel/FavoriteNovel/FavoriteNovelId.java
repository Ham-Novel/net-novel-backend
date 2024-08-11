package com.ham.netnovel.favoriteNovel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteNovelId {
    private Long memberId;
    private Long novelId;

    @Override
    public boolean equals(Object o) {
        //동일한 객체 참조일 경우 true
        if (this == o) return true;
        //비교 대상 객체가 null 이거나, 서로 다른 클래스 타입이면 false
        if (o == null || getClass() != o.getClass()) return false;
        //비교 대상 객체와 내부 프로퍼티 값이 같으면 true
        FavoriteNovelId that = (FavoriteNovelId) o;
        return  Objects.equals(this.memberId, that.memberId) && Objects.equals(this.novelId, that.novelId);
    }

    @Override
    public int hashCode() { return Objects.hash(memberId, novelId);}
}
