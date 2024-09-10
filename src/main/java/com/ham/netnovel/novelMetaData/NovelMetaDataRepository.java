package com.ham.netnovel.novelMetaData;

import com.ham.netnovel.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NovelMetaDataRepository extends JpaRepository<NovelMetaData, Long> {


    @Query("select md " +
            "from NovelMetaData md " +
            "where md.novel.id in :novelIds")
    List<NovelMetaData> findByNovelIds(@Param("novelIds") Set<Long> novelIds);



    Optional<NovelMetaData> findByNovel(Novel novel);


    /**
     * 소설의 ID 로 소설의 메타 데이터 엔티티를  찾는 메서드 입니다.
     * @param novelId 검색할 소설의 ID 값
     * @return Optional 형태의 객체로 반환
     */
    @Query("select md " +
            "from NovelMetaData md " +
            "where md.novel.id =:novelId")
    Optional<NovelMetaData> findByNovelId(@Param("novelId") Long novelId);


}
