package com.ham.netnovel.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {


    //unique 속성의 name 프로퍼티로 Tag 조회
    Optional<Tag> findByName(String name);

    //unique 속성의 name 값 중복 확인
    boolean existsByName(String name);
}
