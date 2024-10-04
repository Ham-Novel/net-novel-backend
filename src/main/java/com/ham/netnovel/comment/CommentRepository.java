package com.ham.netnovel.comment;

import com.ham.netnovel.comment.repository.CommentSearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentSearchRepository {

    /**
     * 유저가 작성한 댓글을 모두찾아 반환하는 메서드
     *
     * @param providerId 유저의 providerId 값
     * @return List Comment 엔티티 List로 반환
     */
    @Query("select c from Comment c " +
            "join fetch c.member m " +//Episode 테이블과 join(N:1)
            "join fetch c.episode e " +//Episode 테이블과 join(N:1)
            "where m.providerId =:providerId " +
            "order by c.createdAt DESC ")//생성 시간으로 내림차순 정렬
//날짜순으로 역정렬, 최신순이 위로옴
    List<Comment> findByMember(@Param("providerId") String providerId, Pageable pageable);


    @Query("select c from Comment c " +
            "join fetch c.member m " +//Member 테이블과 join(N:1)
            "join fetch c.episode e " +//Episode 테이블과 join(N:1)
            "where c.episode.id =:episodeId " +
            "and c.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by c.createdAt desc ")//생성 시간으로 내림차순 정렬
//날짜순으로 역정렬, 최신순이 위로옴
    List<Comment> findByEpisodeIdByCreatedAt(@Param("episodeId") Long episodeId, Pageable pageable);


    @Query("select c from Comment c " +
            "join fetch c.member m " +//Member 테이블과 join(N:1)
            "join fetch c.episode e " +
            "where c.episode.id =:episodeId " +
            "and c.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by (select count(cl) from CommentLike  cl " +//CommentLike 엔티티의 commentId 를 count
            "where cl.comment.id = c.id and cl.likeType ='LIKE') desc ")//좋아요 순으로 내림차순으로 정렬(좋아요 많은 댓글이 위로옴)
//날짜순으로 역정렬, 최신순이 위로옴
    List<Comment> findByEpisodeIdByCommentLikes(@Param("episodeId") Long episodeId, Pageable pageable);


    /**
     * 소설(novel)의 에피소드에 달린 댓글을 DB에서 찾는 메서드
     *
     * @param novelId 소설의 PK
     * @return List<Comment>
     */
    @Query("select c from Comment c " +
            "join fetch c.episode e " +
            "join fetch c.member m " +//Member 테이블과 join(N:1)
            "where e.novel.id = :novelId " +
            "and c.status = 'ACTIVE' " + //ACTIVE 상태인 댓글만 가져옴
            "order by c.createdAt desc ")//생성 시간으로 내림차순 정렬
//댓글 생성시간으로 정렬
//날짜순으로 역정렬, 최신순이 위로옴
    List<Comment> findByNovelOrderByCreatedAt(@Param("novelId") Long novelId, Pageable pageable);


    /**
     * 소설(novel)의 에피소드에 달린 댓글을 DB에서 찾는 메서드
     *
     * @param novelId 소설의 PK
     * @return List<Comment>
     */
    @Query("select c from Comment c " +
            "join fetch c.episode e " +
            "join fetch c.member m " +//Member 테이블과 join(N:1)
            "where e.novel.id = :novelId " +
            "and c.status = 'ACTIVE' " +  //ACTIVE 상태인 댓글만 가져옴
            "order by (select count(cl) from CommentLike cl " + //CommentLike 엔티티의 commentId 를 count
            "where cl.comment.id = c.id) desc ")//내림차순으로 정렬(좋아요 많은 댓글이 위로옴)
//날짜순으로 역정렬, 최신순이 위로옴
    List<Comment> findByNovelOrderByCommentLikes(@Param("novelId") Long novelId, Pageable pageable);


}
