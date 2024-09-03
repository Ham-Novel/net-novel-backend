package com.ham.netnovel.novel.repository;

import com.ham.netnovel.novel.data.NovelSortOrder;
import com.ham.netnovel.novel.dto.NovelListDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NovelSearchRepository {

    List<NovelListDto> findNovelsBySearchConditions(NovelSortOrder novelSortOrder, Pageable pageable);



}
