package com.ham.netnovel.favoriteNovel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, FavoriteNovelId> {

    List<FavoriteNovel> findByMemberId(Long memberId);
    
    List<FavoriteNovel> findByNovelId(Long novelId);
}
