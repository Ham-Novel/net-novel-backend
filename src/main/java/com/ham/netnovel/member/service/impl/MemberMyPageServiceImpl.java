package com.ham.netnovel.member.service.impl;

import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.reComment.service.ReCommentService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MemberMyPageServiceImpl implements MemberMyPageService {


    private final CommentService commentService;

    private final ReCommentService reCommentService;

    private final MemberRepository memberRepository;

    public MemberMyPageServiceImpl(CommentService commentService, ReCommentService reCommentService, MemberRepository memberRepository) {
        this.commentService = commentService;
        this.reCommentService = reCommentService;
        this.memberRepository = memberRepository;
    }


    @Override
    public List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId) {
        // 두 개의 스트림을 합치고 정렬한 후 리스트로 변환하여 반환
        return Stream.concat(
                        //댓글(comment) List를 가져와 stream 생성
                        commentService.getMemberCommentList(providerId).stream(),
                        //대댓글(reComment) List를 가져와 stream 생성
                        reCommentService.getMemberReCommentList(providerId).stream()
                )
                //멤버변수인 생성 시간을 기준으로 재정렬
                .sorted(Comparator.comparing(MemberCommentDto::getCreateAt).reversed())
                // 정렬된 스트림을 리스트 형태로 수집하여 반환
                .collect(Collectors.toList());
    }


}
