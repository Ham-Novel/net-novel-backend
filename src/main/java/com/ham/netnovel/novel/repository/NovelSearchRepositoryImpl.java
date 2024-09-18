package com.ham.netnovel.novel.repository;

import com.ham.netnovel.common.exception.RepositoryMethodException;
import com.ham.netnovel.member.QMember;
import com.ham.netnovel.member.data.MemberRole;
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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class NovelSearchRepositoryImpl implements NovelSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager entityManager;


    @Autowired
    public NovelSearchRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<NovelListDto> findNovelsBySearchConditions(NovelSortOrder novelSortOrder,
                                                           Pageable pageable,
                                                           List<Long> tagIds) {
        try {

            QNovelMetaData novelMetaData = QNovelMetaData.novelMetaData;
            QNovel novel = QNovel.novel;
            QNovelTag novelTag = QNovelTag.novelTag;
            QMember member = QMember.member;

            // 파라미터로 받은 조건으로 ORDER BY 조건 생성
            OrderSpecifier<?> orderSpecifier = getOrderSpecifier(novelSortOrder, novelMetaData);

            //조건을 통해 찾은 Novel 엔티티를 DTO로 변환하여 반환하는 쿼리문 생성
            var query = jpaQueryFactory.select(Projections.bean(NovelListDto.class,//DTO에 값을 넣어 반환
                            novel.id.as("id"),
                            novel.title.as("title"),
                            novel.description.as("desc"),
                            member.providerId.as("providerId"),
                            member.nickName.as("authorName"),
                            novelMetaData.totalFavorites.as("totalFavorites"),
                            novelMetaData.totalViews.as("totalView"),
                            novelMetaData.latestEpisodeAt.as("latestUpdateAt"),
                            novel.thumbnailFileName.as("thumbnailUrl")))
                    .from(novel)
                    .join(novel.novelMetaData,novelMetaData)//메타 데이터와 JOIN
                    .join(novel.author, member);//member 테이블과 Join


            // 유저가 선택한 Tag가 있을 경우, Tag 조건을 쿼리 검색 AND 조건으로 추가
            if (!(tagIds == null) && !tagIds.isEmpty()) {
                query.leftJoin(novel.novelTags, novelTag)//left join으로 tag 정보가 없어도 Novel 레코드 반환
                        .where(novelTag.tag.id.in(tagIds))//유저가 선택한 tag id와 관계가 있는 엔티티만 가져옴
                        .groupBy(novel.id)//소설로 그룹화
                        .having(novelTag.tag.id.in(tagIds).count().eq((long) tagIds.size()));
            }

            //페이지네이션 및 정렬조건 추가
            List<NovelListDto> novelListDtos = query.offset(pageable.getOffset())//페이지 시작 지점, 0부터 시작함
                    .limit(pageable.getPageSize())//한페이지에 보여줄 데이터의 개수
                    .orderBy(orderSpecifier)
                    .fetch();


            //소설 DTO에 태그 정보를 추가
            for (NovelListDto dto : novelListDtos) {
                loadTagsForNovel(dto);
            }
            //소설 DTO List 반환
            return novelListDtos;

        } catch (Exception ex) {
            throw new RepositoryMethodException("findNovelsBySearchConditions 메서드 에러" + ex + ex.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<NovelListDto> findBySearchWord(String searchWord, Pageable pageable) {

        //파라미터 null, 공백 체크
        if (searchWord == null || searchWord.trim().isEmpty()) {
            return Collections.emptyList();
        }

        int pageSize = pageable.getPageSize();// 한 페이지에 표시할 항목 수
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize; // 오프셋 계산


        //Native 쿼리문 생성, MySQL 문법 사용
        String queryStr =
                "SELECT n.id as novelId, " +
                        "n.title as novelTitle," +
                        "n.thumbnail_file_name as fileName, " +
                        "nm.total_views as totalViews, " +
                        "nm.total_favorites as totalFavorites, " +
                        "nm.latest_episode_at as latestAt, " +
                        "m.provider_id as providerId, " +
                        "m.nick_name as nick_name " +  // 필요한 필드만 선택
                        "FROM novel n " +
                        "JOIN novel_meta_data nm ON n.id = nm.novel_id " +
                        "JOIN member m ON m.id = n.member_id " +
                        "WHERE MATCH(n.title) AGAINST(:searchWord IN BOOLEAN MODE) " +//index 사용
                        "LIMIT :limit OFFSET :offset";//페이지네이션

        try {
            //네이티브 쿼리 객체 생성
            Query query = entityManager.createNativeQuery(queryStr);

            //매개변수화된 쿼리, 쿼리문에 파라미터 할당
            query.setParameter("searchWord", searchWord);
            query.setParameter("offset", offset);
            query.setParameter("limit", pageSize);

            //쿼리문 실행후 반환된 결과 객체 생성
            List<Object[]> resultList = query.getResultList();

            //DB에서 찾은 결과 DTO로 변환하여 반환
            List<NovelListDto> novelListDtos = resultList.stream()
                    .map(objects -> NovelListDto.builder()
                            .id((Long) objects[0])
                            .title((String) objects[1])
                            .thumbnailUrl((String) objects[2])
                            .totalView(objects[3] != null ? ((Number) objects[3]).longValue() : 0L) // null-safe 변환
                            .totalFavorites(objects[4] != null ? ((Number) objects[4]).intValue() : 0) // null-safe 변환
                            .latestUpdateAt(objects[5] != null ? ((Timestamp) objects[5]).toLocalDateTime() : null) // null-safe 변환
                            .providerId((String) objects[6])
                            .authorName((String) objects[7])
                            .build()).toList();


            for (NovelListDto dto : novelListDtos) {
                loadTagsForNovel(dto);
            }
            return novelListDtos;
        } catch (Exception ex) {
            throw new RepositoryMethodException("findBySearchWord 메서드 에러" + ex + ex.getMessage());
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<NovelListDto> findByAuthorName(String authorName, Pageable pageable) {


        QNovelMetaData novelMetaData = QNovelMetaData.novelMetaData;
        QNovel novel = QNovel.novel;
        QMember member = QMember.member;

        // 서브쿼리로 Member ID 필터링 (작가 이름이 검색 조건에 맞는 ID만 추출, JOIN 오버헤드 방지)
        JPAQuery<Long> subQuery = jpaQueryFactory.select(member.id)
                .from(member)
                .where(member.nickName.contains(authorName)//검색어로 Member 엔티티를 찾음
                        .and(member.role.eq(MemberRole.AUTHOR)));//Member ROLE이 AUTHOR 인 조건 추가

        List<NovelListDto> novelListDtos = jpaQueryFactory.select(Projections.bean(NovelListDto.class,//DTO에 값을 넣어 반환
                        novel.id.as("id"),
                        novel.title.as("title"),
                        member.providerId.as("providerId"),
                        member.nickName.as("authorName"),
                        novelMetaData.totalFavorites.as("totalFavorites"),
                        novelMetaData.totalViews.as("totalView"),
                        novelMetaData.latestEpisodeAt.as("latestUpdateAt"),
                        novel.thumbnailFileName.as("thumbnailUrl")))

                .from(novel)
                .join(novel.novelMetaData)//메타 데이터와 JOIN
                .join(novel.author, member)//멤버 테이블과 조인
                .where(member.id.in(subQuery))  // 서브쿼리로 MemberId 필터링
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(novelMetaData.latestEpisodeAt.desc()) // 정렬 조건 추가
                .fetch();

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
     * <p>
     * 이 메서드는 소설 정렬 기준에 맞춰 정렬 조건을 설정하여 {@link OrderSpecifier}를 반환합니다.
     * 이는 쿼리문에서 ORDER BY 조건을 추가할때 사용합니다.
     * </p>
     *
     * @param novelSortOrder {@link NovelSortOrder} 소설 정렬 기준을 나타내는 열거형
     * @param novelMetaData  {@link QNovelMetaData} 소설 메타데이터 엔티티를 나타내는 QueryDSL Q 클래스
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
