package com.ham.netnovel.novelRating;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NovelRatingId implements Serializable {


    private Long memberId;

    private Long novelId;

    @Override
    public boolean equals(Object o) {
        //파라미터로 받은 객체와 자신을 비교, 동일한 객체일경우 true 반환
        if (this==o) return true;
        //파라미터로 받은 객체가 null 이거나, 현재 클래스와 다른 클래스일경우 false 반환
        if (o==null || getClass() !=o.getClass()) return false;
        //타입 캐스팅
        NovelRatingId that = (NovelRatingId) o;
        //파라미터로 받은 객체와 현재 객체의 필드값 비교, 두 필드값이 모두 동일해야 true 반환
        return Objects.equals(memberId,that.memberId) && Objects.equals(novelId,that.novelId);

    }

    @Override
    public int hashCode() {

        return Objects.hash(memberId, novelId);
    }

}
