package com.ham.netnovel.novelMetaData.service;

import com.ham.netnovel.novelMetaData.NovelMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface NovelMetaDataRepository extends JpaRepository<NovelMetaData, Long> {


    @Query("select md " +
            "from NovelMetaData md " +
            "where md.novel.id in :novelIds")
    List<NovelMetaData> findByNovelId(@Param("novelIds") Set<Long> novelIds);


}
