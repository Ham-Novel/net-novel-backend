package com.ham.netnovel.comment.repository;

import com.ham.netnovel.member.dto.MemberCommentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentSearchRepository {



    List<MemberCommentDto> findCommentByMember(String providerId, Pageable pageable);

}
