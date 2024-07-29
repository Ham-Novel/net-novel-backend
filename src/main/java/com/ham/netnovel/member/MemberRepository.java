package com.ham.netnovel.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {


    /**
     * OAuth 제공자의 유저 Id값으로 유저 레코드를 찾는 메서드
     * @param providerId 유저의 providerId값
     * @return Optional 형태로 반환
     */

    @Query("select m from Member m " +
            "where m.providerId =:providerId")
    Optional<Member> findByProviderId(@Param("providerId")String providerId);


}
