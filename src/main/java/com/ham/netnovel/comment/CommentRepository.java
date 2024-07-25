package com.ham.netnovel.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


    @Query("select c from Comment c " +
            "where c.episode.id =:episodeId")
    List<Comment> findByEpisodeId(@Param("episodeId")Long episodeId);


    /**
     * 유저가 작성한 댓글을 모두찾아 반환하는 메서드
     * @param providerId 유저의 providerId 값
     * @return List Comment 엔티티 List로 반환
     */
    @Query("select c from Comment c " +
            "where c.member.providerId =:providerId " +
            "order by c.createdAt DESC ")//생성 날짜 내림차순 정렬(최신글이 위로오게설정)
    List<Comment> findByMember(@Param("providerId")String providerId);


    /**
     * 소설(novel)의 에피소드에 달린 댓글을 DB에서 찾는 메서드
     * @param novelId 소설의 PK
     * @return List<Comment>
     */
    @Query("select c from Comment c " +
            "where c.episode.id in " +
            "(select e.id from Episode e " +
            "where e.novel.id = :novelId)")
    List<Comment> findByNovel(@Param("novelId")Long novelId);





}
