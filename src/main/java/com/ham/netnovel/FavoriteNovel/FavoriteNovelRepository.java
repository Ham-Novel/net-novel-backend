package com.ham.netnovel.favoriteNovel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, FavoriteNovelId> {

    List<FavoriteNovel> findByMemberId(Long memberId);


    List<FavoriteNovel> findByNovelId(Long novelId);


    /**
     * 특정 소설에 좋아요를 누른 유저들의 providerId 목록을 반환합니다.
     *
     * <p>
     * 이 메서드는 `FavoriteNovel` 엔티티를 조회하여, 주어진 소설 ID와 연관된 모든 유저의 `providerId`를 가져옵니다.
     * </p>
     * <p>
     * `FavoriteNovel` 엔티티는 소설과 유저 간의 관계를 나타내는 조인 테이블입니다. 이 테이블에서 주어진 소설 ID와 관련된
     * 유저들의 `providerId`를 검색하여 리스트 형태로 반환합니다.
     * </p>
     *
     * @param novelId 소설의 고유 식별자 (ID). 이 값으로 해당 소설을 좋아요한 유저들을 조회합니다.
     * @return 주어진 소설 ID와 관련된 유저들의 `providerId`를 포함하는 리스트. 소설에 좋아요를 누른 유저가 없을 경우 빈 리스트를 반환합니다.
     */
    @Query("select f.member.providerId  " +
            "from FavoriteNovel f " +
            "where f.novel.id =:novelId ")
    List<String> findMemberProviderIdsByNovelId(@Param("novelId") Long novelId);


}
