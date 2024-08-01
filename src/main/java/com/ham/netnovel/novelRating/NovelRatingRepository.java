package com.ham.netnovel.novelRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NovelRatingRepository extends JpaRepository<NovelRating,Long> {
    Optional<NovelRating> findById(NovelRatingId novelRatingId);

    List<NovelRating> findByNovelId(@Param("novelId") Long novelId);




}


