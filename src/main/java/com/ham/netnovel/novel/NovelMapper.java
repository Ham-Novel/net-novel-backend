package com.ham.netnovel.novel;

import com.ham.netnovel.novel.dto.NovelResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Novel Entity를 NovelResponseDto로 parsing해주는 static 객체.
 * MapStruct 라이브러리로 구현.
 */
@Mapper
public interface NovelMapper {
    NovelMapper INSTANCE = Mappers.getMapper(NovelMapper.class);

    @Mapping(target = "novelId", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "authorName", source = "author.nickName")
    @Mapping(target = "view", ignore = true)
    @Mapping(target = "favoriteAmount", ignore = true)
    @Mapping(target = "episodeAmount", expression = "java(novel.getEpisodes().size())")
    @Mapping(target = "tags", ignore = true)
    NovelResponseDto parseResponseDto(Novel novel);
}
