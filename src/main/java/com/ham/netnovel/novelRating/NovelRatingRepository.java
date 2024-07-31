package com.ham.netnovel.novelRating;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NovelRatingRepository extends JpaRepository<NovelRating,Long> {
    Optional<NovelRating> findById(NovelRatingId novelRatingId);


}
