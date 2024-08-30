package com.ham.netnovel.episode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode,Long> {

    @Query("select e from Episode e " +
            "join fetch e.novel n " +
            "where n.id = :novelId " +
            "and e.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by e.createdAt desc") //최신순
    List<Episode> findByNovel(@Param("novelId") Long novelId);


    @Query("select e from Episode e " +
            "join fetch e.novel n " +
            "where n.id = :novelId " +
            "and e.status = 'ACTIVE' " //ACTIVE 상태인 댓글만 가져옴
            +"order by e.createdAt desc") //최신순
    List<Episode> findByNovelOrderByCreatedAtDesc(@Param("novelId") Long novelId, Pageable pageable);

    @Query("select e from Episode e " +
            "join fetch e.novel n " +
            "where n.id = :novelId " +
            "and e.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by e.createdAt asc") //최초순
    List<Episode> findByNovelOrderByCreatedAtAsc(@Param("novelId") Long novelId, Pageable pageable);

    @Query("SELECT e FROM Episode e " +
            "WHERE e.novel.id = :novelId AND e.chapter = :chapter")
    Optional<Episode> findByNovelAndChapter(@Param("novelId") Long novelId, @Param("chapter") Integer chapter);

}
