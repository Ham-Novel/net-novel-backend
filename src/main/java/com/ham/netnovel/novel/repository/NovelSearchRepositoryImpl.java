package com.ham.netnovel.novel.repository;

import com.ham.netnovel.novel.QNovel;
import com.ham.netnovel.novel.data.NovelSortOrder;
import com.ham.netnovel.novel.dto.NovelListDto;
import com.ham.netnovel.novelMetaData.QNovelMetaData;
import com.ham.netnovel.novelTag.QNovelTag;
import com.ham.netnovel.tag.QTag;
import com.ham.netnovel.tag.dto.TagDataDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NovelSearchRepositoryImpl implements NovelSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public NovelSearchRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    /**
     * 주어진 정렬 기준과 페이지 정보를 바탕으로 소설 목록을 조회합니다.
     *<p>
     * 이 메서드는 소설의 기본 정보를 조회하고, 각 소설에 대해 태그 정보를 추가로 조회하여 최종 결과를 반환합니다.
     *</p>
     *
     *
     * @param novelSortOrder {@link NovelSortOrder} 소설을 정렬할 기준을 나타내는 열거형 객체
     * @param pageable {@link Pageable} 페이지 정보를 포함하는 객체 (페이지 번호, 페이지 크기 등)
     * @return {@link List<NovelListDto>} 정렬 기준과 페이지 정보에 따라 조회된 소설 목록을 포함하는 리스트
     */
    @Override
    public List<NovelListDto> findNovelsBySearchConditions(NovelSortOrder novelSortOrder, Pageable pageable) {
        QNovelMetaData novelMetaData = QNovelMetaData.novelMetaData;
        QNovel novel = QNovel.novel;
        // 파라미터로 받은 조건으로 ORDER BY 조건 생성
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(novelSortOrder, novelMetaData);

        //조건을 통해 찾은 Novel 엔티티를 DTO로 변환하여 반환
        List<NovelListDto> novelListDtos = jpaQueryFactory.select(Projections.bean(NovelListDto.class,//DTO에 값을 넣어 반환
                        novel.id.as("id"),
                        novel.title.as("title"),
                        novelMetaData.totalFavorites.as("totalFavorites"),
                        novelMetaData.totalViews.as("totalView"),
                        novelMetaData.latestEpisodeAt.as("latestUpdateAt"),
                        novel.thumbnailFileName.as("thumbnailUrl")))
                .from(novel)
                .join(novel.novelMetaData)
                .offset(pageable.getOffset())//페이지 시작 지점, 0부터 시작함
                .limit(pageable.getPageSize())//한페이지에 보여줄 데이터의 개수
                .orderBy(orderSpecifier)
                .fetch();

        //소설 DTO에 태그 정보를 추가
        for (NovelListDto dto : novelListDtos) {
            loadTagsForNovel(dto);
        }
        //소설 DTO List 반환
        return novelListDtos;
    }

    /**
     * 주어진 소설 DTO에 대해 태그 정보를 조회하여 설정합니다.
     *
     * <p>파라미터로 받은 소설에 관련된 태그를 조회하여 해당 소설의 DTO에 태그 정보를 추가합니다.</p>
     *
     * @param dto {@link NovelListDto} 태그 정보를 설정할 소설 DTO
     */
    private void loadTagsForNovel(NovelListDto dto) {

        QNovelTag novelTag = QNovelTag.novelTag;
        QTag tag = QTag.tag;

        //소설의 태그들을 DTO로 변환하여, NovelListDto 에 할당
        List<TagDataDto> tagDataDtos = jpaQueryFactory.select(Projections.bean(TagDataDto.class,
                        tag.id.as("id"),
                        tag.name.as("name"),
                        tag.status.as("status")))
                .from(novelTag)
                .join(tag).on(tag.id.eq(novelTag.tag.id))//NovelTag와 JOIN
                .where(novelTag.novel.id.eq(dto.getId()))
                .fetch();
        dto.setTags(tagDataDtos);
    }

    /**
     * 주어진 정렬 기준에 따라 {@link OrderSpecifier} 객체를 생성합니다.
     *<p>
     *   이 메서드는 소설 정렬 기준에 맞춰 정렬 조건을 설정하여 {@link OrderSpecifier}를 반환합니다.
     *   이는 쿼리문에서 ORDER BY 조건을 추가할때 사용합니다.
     *</p>
     *
     *
     * @param novelSortOrder {@link NovelSortOrder} 소설 정렬 기준을 나타내는 열거형
     * @param novelMetaData {@link QNovelMetaData} 소설 메타데이터 엔티티를 나타내는 QueryDSL Q 클래스
     * @return {@link OrderSpecifier} 주어진 정렬 기준에 따라 설정된 정렬 조건
     * @throws IllegalArgumentException 지원되지 않는 정렬 기준이 제공된 경우 발생
     */
    private OrderSpecifier<?> getOrderSpecifier(NovelSortOrder novelSortOrder,
                                                QNovelMetaData novelMetaData) {
        //novelSortOrder 조건에 따라, ORDER BY 조건 반환
        return switch (novelSortOrder) {
            case LATEST -> new OrderSpecifier<>(Order.DESC, novelMetaData.latestEpisodeAt);
            case VIEWCOUNT -> new OrderSpecifier<>(Order.DESC, novelMetaData.totalViews);
            case FAVORITES -> new OrderSpecifier<>(Order.DESC, novelMetaData.totalFavorites);
            default -> throw new IllegalArgumentException("Unsupported filter: " + novelSortOrder);

        };
    }


}
