package com.ham.netnovel.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {


    //unique 속성의 name 프로퍼티로 Tag 조회
    Optional<Tag> findByName(String name);

    /**
     * 주어진 검색어를 기반으로 태그 이름 목록을 조회하는 메서드 입니다.
     * <p>
     * 태그 이름이 검색어를 포함하는 경우에 해당하는 태그 이름을 반환합니다.
     * 검색어는 부분 일치로 검색됩니다.
     * </p>
     *
     * @param searchWord 검색할 태그의 일부 또는 전체 이름
     * @return 검색어와 일치하는 태그 이름 목록
     */
    @Query("select t.name " +
            "from Tag t " +
            "where t.name like %:searchWord%")
    List<String> findBySearchWord(@Param("searchWord")String searchWord);

    //unique 속성의 name 값 중복 확인
    boolean existsByName(String name);
}
