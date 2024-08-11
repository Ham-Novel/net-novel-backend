package com.ham.netnovel.favoriteNovel.service;

import com.ham.netnovel.favoriteNovel.FavoriteNovelId;

public interface FavoriteNovelService {

    /**
     * 유저 인증 키와 작품 id를 받아서 선호작을 등록하고 삭제하는 메서드. 레코드가 없다면 새로 생성하고 이미 있다면 삭제한다.
     * @param providerId OAuth2.0 유저 인증 키
     * @param novelId  작품 PK 값
     * @return FavoriteNovelId (생성한/삭제한) 레코드 PK 값
     */
    FavoriteNovelId toggleFavoriteNovel(String providerId, Long novelId);
}
