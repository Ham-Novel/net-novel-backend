package com.ham.netnovel.episode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode,Long> {

//    @Query("select c from Comment c " +
//            "join fetch c.member m " +//Member 테이블과 join(N:1)
//            "join fetch c.episode e " +//Episode 테이블과 join(N:1)
//            "where c.episode.id =:episodeId " +
//            "and c.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
//            "order by c.createdAt desc ")//생성 시간으로 내림차순 정렬

    @Query("select e from Episode e " +
            "join fetch e.novel n " +
            "where n.id = :novelId " +
            "and e.status = 'ACTIVE' ") //ACTIVE 상태인 댓글만 가져옴
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

}
