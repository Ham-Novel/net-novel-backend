package com.ham.netnovel.member.service.impl;

import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.service.MemberMyPageService;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import com.ham.netnovel.novel.service.NovelService;
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

    private final NovelService novelService;

    public MemberMyPageServiceImpl(CommentService commentService, ReCommentService reCommentService, MemberRepository memberRepository, NovelService novelService) {
        this.commentService = commentService;
        this.reCommentService = reCommentService;
        this.memberRepository = memberRepository;
        this.novelService = novelService;
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


    //ToDo Author 엔티티 생성 후, 작가 정보도 DTO에 담아서 반환
    @Override
    public List<NovelFavoriteDto> getFavoriteNovelsByMember(String providerId) {
        if (providerId == null || providerId.trim().isEmpty()) {
            throw new IllegalArgumentException("getFavoriteNovelsByMember 파라미터 에러, 파라미터가 비어있음");
        }
        return novelService.getFavoriteNovels(providerId)
                .stream()
                .map(novel -> NovelFavoriteDto.builder()
                        .novelId(novel.getId())
                        .title(novel.getTitle())
                        .status(novel.getStatus())
                        .build()
                ).collect(Collectors.toList());
    }


}
