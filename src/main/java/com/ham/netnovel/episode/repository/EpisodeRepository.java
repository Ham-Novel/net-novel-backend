package com.ham.netnovel.episode.repository;

import com.ham.netnovel.episode.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode,Long>, EpisodeSearchRepository {

    @Query("select e from Episode e " +
            "join fetch e.novel n " +
            "where n.id = :novelId " +
            "and e.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by e.createdAt desc") //최신순
    List<Episode> findByNovel(@Param("novelId") Long novelId);

    @Query("SELECT e FROM Episode e " +
            "WHERE e.novel.id = :novelId AND e.chapter = :chapter")
    Optional<Episode> findByNovelAndChapter(@Param("novelId") Long novelId, @Param("chapter") Integer chapter);


}
