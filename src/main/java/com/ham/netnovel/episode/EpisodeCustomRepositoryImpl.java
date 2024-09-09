package com.ham.netnovel.episode;

import com.ham.netnovel.novel.QNovel;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class EpisodeCustomRepositoryImpl implements EpisodeCustomRepository {

    private final JPQLQueryFactory jpaQueryFactory;

    public EpisodeCustomRepositoryImpl(JPQLQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<Episode> findEpisodesByConditions(String sortBy, Long novelId, Pageable pageable) {
        QNovel novel = QNovel.novel;
        QEpisode episode = QEpisode.episode;


        return jpaQueryFactory.select(episode)
                .from(episode)
                .join(novel).on(episode.novel.id.eq(novel.id))
                .where(episode.novel.id.eq(novelId))
                .orderBy(getOrderSpecifier(sortBy))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        QEpisode episode = QEpisode.episode;

        return switch (sortBy) {
            case "recent" -> new OrderSpecifier<>(Order.DESC, episode.createdAt);
            case "initial" -> new OrderSpecifier<>(Order.ASC, episode.createdAt);
            default -> throw new IllegalArgumentException("필터가 올바르지 않습니다, 파라미터 :  " + sortBy);

        };
    }
}
