package com.ham.netnovel.reComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {


    @Query("select r from ReComment r " +
            "where r.comment.id = :commentId")
    List<ReComment> findByCommentId(@Param("commentId")Long commentId);


    @Query("select r from ReComment  r " +
            "where r.member.providerId =:providerId " +
            "order by r.createdAt desc ")
    List<ReComment> findByMember(@Param("providerId")String providerId);



}
