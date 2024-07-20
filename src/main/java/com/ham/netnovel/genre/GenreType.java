package com.ham.netnovel.genre;

public enum GenreType {

    FANTASY("판타지"),
    SCIENCE_FICTION("공상과학"),
    MYSTERY("미스터리"),
    ROMANCE("로맨스"),
    HORROR("호러"),
    THRILLER("스릴러"),
    ADVENTURE("모험");

    // 한글 이름을 저장하는 필드
    private final String koreanName;

    // Enum 생성자
    GenreType(String koreanName) {
        this.koreanName = koreanName;
    }

    // 한글 이름을 반환하는 메서드
    public String getKoreanName() {
        return koreanName;
    }


}
