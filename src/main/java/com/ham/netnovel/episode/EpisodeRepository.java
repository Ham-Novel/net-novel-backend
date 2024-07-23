package com.ham.netnovel.episode;


import com.ham.netnovel.episode.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode,Long> {


    @Query("select e from Episode e " +
            "where e.novel.id =:novelId")
    List<Episode> findByNovel(@Param("novelId")Long novelId);
}
