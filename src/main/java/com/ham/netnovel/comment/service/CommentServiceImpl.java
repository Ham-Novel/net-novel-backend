package com.ham.netnovel.comment.service;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentRepository;
import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.dto.CommentDeleteDto;
import com.ham.netnovel.comment.dto.CommentEpisodeListDto;
import com.ham.netnovel.comment.dto.CommentUpdateDto;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;

    private final MemberService memberService;

    private final EpisodeService episodeService;

    public CommentServiceImpl(CommentRepository commentRepository, MemberService memberService, EpisodeService episodeService) {
        this.commentRepository = commentRepository;
        this.memberService = memberService;
        this.episodeService = episodeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    @Override
    public void createComment(CommentCreateDto commentCreateDto) {
        log.info("댓글정보={}", commentCreateDto.toString());
        //Member 엔티티 조회, null이면 예외로 던짐
        Member member = memberService.getMember(commentCreateDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("Member 정보 없음"));

        //Member 엔티티 조회, null이면 예외로 던짐
        Episode episode = episodeService.getEpisodeEntity(commentCreateDto.getEpisodeId())
                .orElseThrow(() -> new NoSuchElementException("Episode 정보 없음"));

        try {
            //Comment 엔티티 생성
            Comment comment = new Comment(commentCreateDto.getContent(), episode, member);
            //Comment 엔티티 저장
            commentRepository.save(comment);

        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createComment 메서드 에러 발생"); // 예외 던지기
        }
    }

    //ToDo 예외처리 구체적으로 작성
    @Override
    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto) {
        log.info("댓글정보={}", commentUpdateDto.toString());
        //null 체크, null이면 예외로 던짐
        Comment comment = getComment(commentUpdateDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("댓글 정보 없음"));
        try {
            String providerId = comment.getMember().getProviderId();

            Long episodeId = comment.getEpisode().getId();

            //댓글 수정 요청자와 댓글 작성자가 일치하는지 확인
            //댓글 수정 에피소드와 기존 댓글과 mapping된 에피소드가 일치하는지 확인
            if (providerId.equals(commentUpdateDto.getProviderId()) && Objects.equals(episodeId, commentUpdateDto.getEpisodeId())) {
                comment.updateComment(commentUpdateDto.getContent());
                commentRepository.save(comment);

            } else {
                log.error("updateComment 메서드 에러 발생,  잘못된 파라미터 입력");
                throw new IllegalArgumentException("updateComment,  잘못된 파라미터 입력");
            }

        } catch (Exception ex) {
            //그외 예외처리
            throw new ServiceMethodException("updateComment 메서드 에러 발생"); // 예외 던지기
        }


    }

    @Override
    @Transactional
    public void deleteComment(CommentDeleteDto commentDeleteDto) {
        //null 체크, null이면 예외로 던짐
        Comment comment = getComment(commentDeleteDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("댓글 정보 없음"));
        try {
            String providerId = comment.getMember().getProviderId();

            Long episodeId = comment.getEpisode().getId();

            //댓글 삭제 요청자와 댓글 작성자가 일치하는지 확인
            //댓글 삭제 에피소드와 기존 댓글과 mapping된 에피소드가 일치하는지 확인
            if (providerId.equals(commentDeleteDto.getProviderId()) && Objects.equals(episodeId, commentDeleteDto.getEpisodeId())) {

                //엔티티의 상태를 삭제 상태로 변경
                comment.changeStatus(CommentStatus.DELETED_BY_USER);

                //변경된 엔티티를 저장
                commentRepository.save(comment);

            } else {
                throw new IllegalArgumentException("deleteComment 메서드 에러 발생, 잘못된 파라미터 입력");
            }

        } catch (Exception ex) {
            //그외 예외처리
            throw new ServiceMethodException("deleteComment 메서드 에러 발생"); // 예외 던지기
        }


    }

    //ToDo 댓글 페이지네이션
    @Override
    @Transactional(readOnly = true)
    public List<MemberCommentDto> getMemberCommentList(String providerId) {

        try {
            //유저가 작성한 댓글이 있으면, DTO로 변환해서 반환
            return commentRepository.findByMember(providerId)
                    .stream()
                    .map(comment -> MemberCommentDto.builder()
                            .type(CommentType.COMMENT)//타입지정
                            .id(comment.getId())
                            .content(comment.getContent())
                            .createAt(comment.getCreatedAt())
                            .updatedAt(comment.getUpdatedAt())
                            .build())
                    //생성시간 역순으로 정렬(최신 댓글이 먼저 나오도록)
                    .sorted(Comparator.comparing(MemberCommentDto::getCreateAt).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ServiceMethodException("getMemberCommentList 메서드 에러 발생"); // 예외 던지기
        }

    }



    //ToDo 댓글 페이지네이션
    @Override
    @Transactional(readOnly = true)
    public List<CommentEpisodeListDto> getEpisodeCommentListByRecent(Long episodeId) {

        try {
            return commentRepository.findByEpisodeId(episodeId)
                    .stream()
                    //엔티티 DTO로 convert
                    .map(this::convertToCommentEpisodeListDto)
                    //생성시간 역순으로 정렬(최신 댓글이 먼저 나오도록)
                    .sorted(Comparator.comparing(CommentEpisodeListDto::getCreatedAt).reversed())
                    .collect(Collectors.toList()); // List로 변환

        } catch (Exception e) {
            throw new ServiceMethodException("getReCommentList 메서드 에러 발생"); // 예외 던지기
        }


    }
    //ToDo 댓글 페이지네이션
    @Override
    @Transactional(readOnly = true)
    public List<CommentEpisodeListDto> getEpisodeCommentListByLikes(Long episodeId) {
        try {
            return commentRepository.findByEpisodeId(episodeId)
                    .stream()
                    //엔티티 DTO로 convert
                    .map(this::convertToCommentEpisodeListDto)
                    //좋아요 순으로 정렬, 기본값은 오름차순 정렬이므로 reversed 추가
                    .sorted(Comparator.comparing(CommentEpisodeListDto::getLikes).reversed())
                    .collect(Collectors.toList()); // List로 변환

        } catch (Exception ex) {
            throw new ServiceMethodException("getReCommentList 메서드 에러 발생" + ex.getMessage()); // 예외 던지기
        }
    }



    //ToDo 댓글 페이지네이션
    @Override
    @Transactional(readOnly = true)
    public List<CommentEpisodeListDto> getNovelCommentListByRecent(Long novelId) {
        try {
            return commentRepository.findByNovel(novelId).stream()
                //엔티티 DTO로 convert
                .map(this::convertToCommentEpisodeListDto)
                //생성시간 역순으로 정렬(최신 댓글이 먼저 나오도록)
                .sorted(Comparator.comparing(CommentEpisodeListDto::getCreatedAt).reversed())
                .collect(Collectors.toList());

        }catch (Exception e) {
            throw new ServiceMethodException("getMemberCommentList 메서드 에러 발생"); // 예외 던지기
        }

    }

    //ToDo 댓글 페이지네이션
    @Override
    @Transactional(readOnly = true)
    public List<CommentEpisodeListDto> getNovelCommentListByLikes(Long novelId) {
        try {
            return commentRepository.findByNovel(novelId).stream()
                    //엔티티 DTO로 convert
                    .map(this::convertToCommentEpisodeListDto)
                    //좋아요 순으로 정렬, 기본값은 오름차순 정렬이므로 reversed 추가
                    .sorted(Comparator.comparing(CommentEpisodeListDto::getLikes).reversed())
                    .collect(Collectors.toList());
        }catch (Exception e) {
            throw new ServiceMethodException("getMemberCommentList 메서드 에러 발생"); // 예외 던지기
        }

    }


    /**
     * Comment 엔티티를 CommentEpisodeListDto로 convert하는 메서드
     * 댓글(Comment)에 달린 대댓글(reComment) 정보도 List로 포함됨
     *
     * @param comment 파라미터로 받을 댓글 엔티티
     * @return CommentEpisodeListDto DTO로 변환하여 반환
     */

    private CommentEpisodeListDto convertToCommentEpisodeListDto(Comment comment) {

        return CommentEpisodeListDto.builder()
                .nickName(comment.getMember().getNickName())//작성자 닉네임
                .episodeTitle(comment.getEpisode().getTitle())//에피소드 제목
                .content(comment.getContent())
                .commentId(comment.getId())
                .updatedAt(comment.getUpdatedAt())
                .createdAt(comment.getCreatedAt())
                .likes(comment.getTotalLikes())//댓글에 달린  좋아요 수
                .disLikes(comment.getTotalDisLikes())//댓글에 달린 싫어요 수
                .reCommentList(Optional.ofNullable(comment.getReComments())//대댓글 null 체크
                        .orElse(Collections.emptyList()) // null일 경우 빈 리스트 반환
                        .stream()//연관된 대댓글 엔티티를 DTO 형태로 변환하여 List로 반환
                        .map(reComment -> ReCommentListDto.builder()
                                .nickName(reComment.getMember().getNickName())
                                .content(reComment.getContent())
                                .reCommentId(reComment.getComment().getId())
                                .createdAt(reComment.getCreatedAt())
                                .updatedAt(reComment.getUpdatedAt())
                                .likes(reComment.getTotalLikes())//댓글에 달린 싫어요 수
                                .disLikes(reComment.getTotalDisLikes())//대댓글에 달린 싫어요 수
                                .build())
                        .collect(Collectors.toList())) // List로 변환
                .build();
    }


}
