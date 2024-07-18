package com.ham.netnovel.comment.service;


import com.ham.netnovel.comment.Comment;
import com.ham.netnovel.comment.CommentRepository;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.Service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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


    @Transactional
    @Override
    public boolean createComment(CommentCreateDto commentCreateDto) {

        log.info("댓글정보={}",commentCreateDto.toString());
        try {
            //Member 엔티티 조회, null이면 예외로 던짐
            Member member = memberService.getMember(commentCreateDto.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Member 정보 없음"));

            //Member 엔티티 조회, null이면 예외로 던짐
            Episode episode = episodeService.getEpisode(commentCreateDto.getEpisodeId())
                    .orElseThrow(() -> new RuntimeException("episode 정보 없음"));


            //Comment 엔티티 생성
            Comment comment = new Comment(commentCreateDto.getContent(), episode, member);


            //Comment 엔티티 저장
            commentRepository.save(comment);

            return true;


        } catch (DataAccessException ex) {
            // 데이터베이스 저장 과정에서 예외 발생 시 처리
            log.error("Failed to save comment to database: {}", ex.getMessage());
            throw new RuntimeException("Comment 생성 에러 발생"); // 예외 던지기
        } catch (Exception ex) {
            //그외 예외처리
            log.error("Failed to save comment to database: {}", ex.getMessage());
            throw new RuntimeException("Failed to save comment. Please try again later."); // 예외 던지기
        }
    }
}
