package com.ham.netnovel.reCommentLike;


import com.ham.netnovel.reComment.ReCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReCommentLikeRepository extends JpaRepository<ReCommentLike,Long> {
    Optional<ReCommentLike> findById(ReCommentLikeId id);


}
