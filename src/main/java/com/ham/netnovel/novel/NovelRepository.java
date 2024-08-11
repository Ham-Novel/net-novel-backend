package com.ham.netnovel.novel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {


    @Query("select n from Novel n " +
            "join fetch n.author m " +
            "where m.id = :providerId")
    List<Novel> findNovelsByMember(@Param("providerId") String providerId);



    @Query("select n from Novel n " +
            "where n.id in " +
            "(select fn.novel.id from FavoriteNovel fn " +
            "where fn.member.providerId =:providerId)")
    List<Novel> findFavoriteNovelsByMember(@Param("providerId") String providerId);




    @Query("select distinct n " +
            "from Novel n " +
            "where n in " +
            "(select r.novel from NovelRating r " +
            "where r.rating is not null)")
    List<Novel> findByNovelRating();


    @Query("""
            SELECT DISTINCT n FROM Novel n
            JOIN FETCH n.episodes e
            WHERE e.chapter = (
                SELECT MAX(es.chapter)
                FROM Episode es
                WHERE es.novel.id = n.id
            )
            ORDER BY e.createdAt DESC
            """)
    List<Novel> findByLatestEpisodesOrderByCreatedAt(Pageable pageable);

}
