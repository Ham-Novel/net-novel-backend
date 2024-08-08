package com.ham.netnovel.novelTag;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface NovelTagRepository extends JpaRepository<NovelTag, Long> {

    Optional<NovelTag> findById(NovelTagId id);

//    @Query("SELECT nt FROM NovelTag nt " +
//            "WHERE nt.id.novelId = :novelId")
    List<NovelTag> findByIdNovelId(Long novelId);
}
