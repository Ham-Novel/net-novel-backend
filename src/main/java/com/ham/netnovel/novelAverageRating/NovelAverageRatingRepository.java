package com.ham.netnovel.novelAverageRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NovelAverageRatingRepository extends JpaRepository<NovelAverageRating,Long> {



    @Query("select na from NovelAverageRating na " +
            "where na.novel.id = :novelId")
    Optional<NovelAverageRating> findByNovelId(@Param("novelId") Long novelId);



}
