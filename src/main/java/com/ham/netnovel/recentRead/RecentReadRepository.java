package com.ham.netnovel.recentRead;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RecentReadRepository extends JpaRepository<RecentRead,RecentReadId> {


    List<RecentRead> findByMemberProviderId(@Param("providerId") String providerId,
                                            Pageable pageable);




}
