package com.ham.netnovel.recentRead;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RecentReadRepository extends JpaRepository<RecentRead,RecentReadId> {


    @Query("select rr " +
            "from RecentRead rr " +
            "join fetch rr.member m " +
            "join fetch rr.episode e " +
            "where rr.member.providerId =:providerId " +
            "order by rr.createdAt desc ")
    List<RecentRead> findByMemberProviderId(@Param("providerId") String providerId,
                                            Pageable pageable);




}
