package com.ham.netnovel.reComment.repository;

import com.ham.netnovel.comment.QComment;
import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.commentLike.data.LikeType;
import com.ham.netnovel.episode.QEpisode;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.QNovel;
import com.ham.netnovel.reComment.QReComment;
import com.ham.netnovel.reComment.QReCommentLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ReCommentSearchRepositoryImpl implements ReCommentSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public ReCommentSearchRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    @Transactional(readOnly = true)
    public List<MemberCommentDto> findReCommentByMember(String providerId, Pageable pageable) {


        QReComment reComment = QReComment.reComment;
        QEpisode episode = QEpisode.episode;
        QReCommentLike reCommentLike = QReCommentLike.reCommentLike;
        QNovel novel = QNovel.novel;
        QComment comment = QComment.comment;


        //원하는 값을 찾아와 DTO에 할당하여 반환
        return jpaQueryFactory.select(
                        reComment.id,
                        reComment.content,
                        reComment.createdAt,
                        episode.id,
                        episode.title,
                        reCommentLike.likeType.when(LikeType.LIKE).then(1).otherwise(0).sum(), // as 없이 사용
                        reCommentLike.likeType.when(LikeType.DISLIKE).then(1).otherwise(0).sum(), // as 없이 사용
                        novel.title,
                         novel.id
                )
                .from(reComment)
                .join(reComment.comment, comment)
                .join(reComment.comment.episode, episode)
                .leftJoin(reComment.reCommentLikes, reCommentLike) // 좋아요/싫어요 테이블 조인
                .where(reComment.member.providerId.eq(providerId))
                .groupBy(reComment.id)
                .fetch()
                .stream()
                .map(tuple -> MemberCommentDto.builder()//DTO로 변환
                        .id(tuple.get(reComment.id))
                        .content(tuple.get(reComment.content))
                        .episodeId(tuple.get(episode.id))
                        .episodeTitle(tuple.get(episode.title))
                        .createdAt(tuple.get(reComment.createdAt))
                        .novelTitle(tuple.get(novel.title))
                        .novelId(tuple.get(novel.id))
                        .likes(Optional.ofNullable(tuple.get(5, Integer.class)).orElse(0)) // null 방지
                        .disLikes(Optional.ofNullable(tuple.get(6, Integer.class)).orElse(0)) // null 방지
                        .isEditable(true)
                        .type(CommentType.RECOMMENT)
                        .build()).toList();

    }


}
