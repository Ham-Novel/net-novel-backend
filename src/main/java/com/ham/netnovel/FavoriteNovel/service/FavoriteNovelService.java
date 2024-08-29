package com.ham.netnovel.favoriteNovel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.favoriteNovel.FavoriteNovelId;

import java.util.List;

public interface FavoriteNovelService {

    /**
     * 유저 인증 키와 작품 id를 받아서 선호작을 등록하고 삭제하는 메서드. 레코드가 없다면 새로 생성하고 이미 있다면 삭제한다.
     * @param providerId OAuth2.0 유저 인증 키
     * @param novelId  작품 PK 값
     * @return FavoriteNovelId (생성한/삭제한) 레코드 PK 값
     */
    Boolean toggleFavoriteNovel(String providerId, Long novelId);

    Boolean checkFavorite(String providerId, Long novelId);


    /**
     * 주어진 소설 ID에 대해 구독 중인 유저의 providerId를 반환합니다.
     *
     * <p>이 메서드는 소설에 좋아요를 누른 유저들의 providerId를 리스트 형태로 반환합니다.
     * 주어진 소설 ID가 null인 경우에는 {@link IllegalArgumentException}을 발생시킵니다.
     * </p>
     *
     * <p>
     *
     * </p>또한, 데이터베이스 접근 중 예외가 발생할 경우에는 {@link ServiceMethodException}을 발생시킵니다.
     * @param novelId 소설의 ID (null일 수 없음)
     * @return 소설에 좋아요를 누른 유저들의 providerId 리스트
     * @throws IllegalArgumentException 소설 ID가 null인 경우 발생
     * @throws ServiceMethodException 데이터베이스 접근 중 예외가 발생한 경우
     */
    List<String> getSubscribedMemberProviderIds(Long novelId);

}
