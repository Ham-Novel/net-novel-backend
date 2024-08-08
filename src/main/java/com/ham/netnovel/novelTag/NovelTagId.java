package com.ham.netnovel.novelTag;

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
//NovelTag composite PK 정의
public class NovelTagId implements Serializable {
    private Long novelId;
    private Long tagId;

    //객체 동등성 판단
    @Override
    public boolean equals(Object o) {
        //동일한 객체 참조일 경우 true
        if (this == o) return true;
        //비교 대상 객체가 null 이거나, 서로 다른 클래스 타입이면 false
        if (o == null || getClass() != o.getClass()) return false;
        //비교 대상 객체와 내부 프로퍼티 값이 같으면 true
        NovelTagId that = (NovelTagId) o;
        return  Objects.equals(this.novelId, that.novelId) && Objects.equals(this.tagId, that.tagId);
    }

    @Override
    public int hashCode() { return Objects.hash(novelId, tagId);}
}
