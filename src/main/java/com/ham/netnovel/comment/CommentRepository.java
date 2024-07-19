package com.ham.netnovel.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


    @Query("select c from Comment c " +
            "where c.episode.id =:episodeId")
    List<Comment> findByEpisodeId(@Param("episodeId")Long episodeId);
}
