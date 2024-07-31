package com.ham.netnovel.commentLike;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {


    Optional<CommentLike> findById(CommentLikeId id);
}
