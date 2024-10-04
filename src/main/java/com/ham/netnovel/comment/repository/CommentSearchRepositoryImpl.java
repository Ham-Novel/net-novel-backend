package com.ham.netnovel.comment.repository;


import com.ham.netnovel.comment.QComment;
import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.commentLike.QCommentLike;
import com.ham.netnovel.commentLike.data.LikeType;
import com.ham.netnovel.episode.QEpisode;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.QNovel;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class CommentSearchRepositoryImpl implements CommentSearchRepository {


    private final JPAQueryFactory jpaQueryFactory;


    @Autowired
    public CommentSearchRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<MemberCommentDto> findCommentByMember(String providerId, Pageable pageable) {


        QComment comment = QComment.comment;
        QEpisode episode = QEpisode.episode;
        QCommentLike commentLike = QCommentLike.commentLike;
        QNovel novel = QNovel.novel;

        return jpaQueryFactory.select(
                        comment.id,
                        comment.content,
                        comment.createdAt,
                        episode.id,
                        episode.title,
                        commentLike.likeType.when(LikeType.LIKE).then(1).otherwise(0).sum(), // as 없이 사용
                        commentLike.likeType.when(LikeType.DISLIKE).then(1).otherwise(0).sum(), // as 없이 사용
                        novel.title,
                        novel.id

                )
                .from(comment)
                .join(comment.episode, episode)
                .join(comment.episode.novel, novel)
                .leftJoin(comment.commentLikes, commentLike) // 좋아요/싫어요 테이블 조인
                .where(comment.member.providerId.eq(providerId))
                .groupBy(comment.id)
                .fetch()
                .stream()
                .map(tuple -> MemberCommentDto.builder()
                        .id(tuple.get(comment.id))
                        .content(tuple.get(comment.content))
                        .episodeId(tuple.get(episode.id))
                        .episodeTitle(tuple.get(episode.title))
                        .createdAt(tuple.get(comment.createdAt))
                        .novelTitle(tuple.get(novel.title))
                        .novelId(tuple.get(novel.id))
                        .likes(Optional.ofNullable(tuple.get(5, Integer.class)).orElse(0)) // 좋아요수 할당null 방지
                        .disLikes(Optional.ofNullable(tuple.get(6, Integer.class)).orElse(0)) // 싫어요수 할당 null 방지
                        .isEditable(true)
                        .type(CommentType.COMMENT)
                        .build()).toList();

    }
}
