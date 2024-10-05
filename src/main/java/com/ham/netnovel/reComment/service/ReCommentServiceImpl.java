package com.ham.netnovel.reComment.service;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentStatus;
import com.ham.netnovel.comment.data.CommentType;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.ReComment;
import com.ham.netnovel.reComment.repository.ReCommentRepository;
import com.ham.netnovel.reComment.dto.ReCommentCreateDto;
import com.ham.netnovel.reComment.dto.ReCommentDeleteDto;
import com.ham.netnovel.reComment.dto.ReCommentListDto;
import com.ham.netnovel.reComment.dto.ReCommentUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReCommentServiceImpl implements ReCommentService {

    private final ReCommentRepository reCommentRepository;
    private final MemberService memberService;
    private final CommentService commentService;

    public ReCommentServiceImpl(ReCommentRepository reCommentRepository, MemberService memberService, CommentService commentService) {
        this.reCommentRepository = reCommentRepository;
        this.memberService = memberService;
        this.commentService = commentService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReComment> getReComment(Long reCommentId) {
        return reCommentRepository.findById(reCommentId);
    }

    @Override
    @Transactional
    public void createReComment(ReCommentCreateDto reCommentCreateDto) {
        //Member 엔티티 조회, null 이면 예외로 던짐
        Member member = memberService.getMember(reCommentCreateDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("createReComment 에러, Member 정보가 없습니다."
                        + reCommentCreateDto.getProviderId()));

        //comment 엔티티 조회, null 이면 예외로 던짐
        Comment comment = commentService.getComment(reCommentCreateDto.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("createReComment 에러, comment 정보가 없습니다."
                        + reCommentCreateDto.getCommentId()));
        try {
            //ReComment 엔티티 생성
            ReComment reComment = ReComment.builder()
                    .comment(comment)
                    .member(member)
                    .content(reCommentCreateDto.getContent())
                    .build();
            //ReComment 엔티티 저장
            reCommentRepository.save(reComment);

        } catch (Exception ex) {
            throw new ServiceMethodException("createReComment 메서드 에러 발생" + ex + ex.getMessage()); // 예외 던지기
        }
    }

    @Override
    @Transactional
    public void updateReComment(ReCommentUpdateDto reCommentUpdateDto) {
        //대댓글 null 체크, null이면 예외로 던짐
        ReComment reComment = getReComment(reCommentUpdateDto.getReCommentId())
                .orElseThrow(() -> new NoSuchElementException("updateReComment 에러, ReComment 정보가 없습니다. reCommentId ="
                        + reCommentUpdateDto.getReCommentId()));

        //대댓글 작성한 유저 정보
        String providerId = reComment.getMember().getProviderId();
        //댓글의 Id 값
        Long commentId = reComment.getComment().getId();
        // 댓글 수정 요청자와 댓글 작성자가 일치하는지 확인
        // 댓글 수정 에피소드와 기존 댓글과 매핑된 에피소드가 일치하는지 확인
        if (!providerId.equals(reCommentUpdateDto.getProviderId()) &&
                !commentId.equals(reCommentUpdateDto.getCommentId())) {
            throw new IllegalArgumentException("잘못된 파라미터 입력: 수정 요청자와 작성자가 일치하지 않거나 잘못된 댓글 ID 입니다. " +
                    "요청자 providerId="+ reCommentUpdateDto.getProviderId());
        }
        //엔티티 필드값 제거
        reComment.updateReComment(reCommentUpdateDto.getContent());
        try {
            reCommentRepository.save(reComment);
        } catch (Exception ex) {
            //그외 예외처리
            throw new ServiceMethodException("updateReComment 메서드 에러 발생" + ex + ex.getMessage()); // 예외 던지기
        }
    }

    @Override
    @Transactional
    public void deleteReComment(ReCommentDeleteDto commentDeleteDto) {
        ReComment reComment = getReComment(commentDeleteDto.getReCommentId())
                .orElseThrow(() -> new NoSuchElementException("deleteReComment 에러, ReComment 정보가 없습니다. reCommentId ="
                        + commentDeleteDto.getReCommentId()));
        try {
            //대댓글 작성한 유저 정보
            String providerId = reComment.getMember().getProviderId();
            //대댓글에 mapping된 댓글의 Id 값
            Long commentId = reComment.getComment().getId();

            //대댓글 삭제 요청자와 댓글 작성자가 일치하는지 확인
            //대댓글 삭제 댓글 mapping된 댓글이 일치하는지 확인
            if (providerId.equals(commentDeleteDto.getProviderId())
                    && Objects.equals(commentId, commentDeleteDto.getCommentId())) {
                //대댓글을 삭제 상태로 변경
                reComment.changeReStatus(CommentStatus.DELETED_BY_USER);
                //변경된 레코드 DB에 저장
                reCommentRepository.save(reComment);
            } else {
                throw new IllegalArgumentException("deleteReComment 메서드 에러 발생, 잘못된 파라미터 입력");
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("deleteReComment 메서드 에러 발생"+ex+ex.getMessage()); // 예외 던지기
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReCommentListDto> getReCommentList(Long commentId) {
        try {
            return reCommentRepository.findByCommentId(commentId)
                    .stream().map(reComment -> ReCommentListDto.builder()
                            .nickName(reComment.getMember().getNickName())
                            .content(reComment.getContent())
                            .reCommentId(reComment.getComment().getId())
                            .updatedAt(reComment.getUpdatedAt())
                            .build())
                    .sorted(Comparator.comparing(ReCommentListDto::getCreatedAt).reversed())
                    .toList();


        } catch (Exception e) {
            throw new ServiceMethodException("getReCommentList 메서드 에러 발생"); // 예외 던지기

        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCommentDto> getMemberReCommentList(String providerId, Pageable pageable) {

        try {

           return reCommentRepository.findReCommentByMember(providerId, pageable);

        } catch (Exception ex) {
            throw new ServiceMethodException("getMemberReCommentList 메서드 에러 발생"+ex+ex.getMessage()); // 예외 던지기
        }


    }

    private MemberCommentDto convertToMemberCommentDto(ReComment recomment) {
        if (recomment==null) {
            throw new IllegalArgumentException("convertToMemberCommentDto 에러, 파라미터가 null 입니다.");
        }
        Episode episode = recomment.getComment().getEpisode();
        if (episode == null || episode.getNovel() == null) {
            throw new IllegalArgumentException("convertToMemberCommentDto 에러: Episode 또는 Novel이 null입니다.");
        }
        return  MemberCommentDto.builder()
                .type(CommentType.RECOMMENT)//타입지정
                .id(recomment.getId())
                .content(recomment.getContent())
                .novelTitle(episode.getNovel().getTitle())
                .episodeId(episode.getId())//리다이렉트용 ID
                .episodeTitle(episode.getTitle())
                .createdAt(recomment.getCreatedAt())
                .isEditable(true)//수정가능여부 true
                .likes(recomment.getTotalLikes())
                .disLikes(recomment.getTotalDisLikes())
                .build();

    }

}
