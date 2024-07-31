package com.ham.netnovel.commentLike;

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
public class CommentLikeId implements Serializable {//composite PK 정의 class
    private Long commentId;
    private Long memberId;

    //객체 동등성 판단
    @Override
    public boolean equals(Object o) {
        //파라미터로 받은 객체와 자신을 비교, 동일한 객체일경우 true 반환
        if (this == o) return true;
        //파라미터로 받은 객체가 null 이거나, 현재 클래스와 다른 클래스일경우 false 반환
        if (o == null || getClass() != o.getClass()) return false;
        //타입 캐스팅
        CommentLikeId that = (CommentLikeId) o;
        //파라미터로 받은 객체와 현재 객체의 필드값 비교, 두 필드값이 모두 동일해야 true 반환
        return Objects.equals(commentId, that.commentId) && Objects.equals(memberId, that.memberId);    }

    //equals()가 true를 반환할때, 객체들이 도일한 해시 코드 값을 반환하도록 보장하는 메서드
    @Override
    public int hashCode() {
        return Objects.hash(commentId, memberId);
    }
}
