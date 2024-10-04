package com.ham.netnovel.reComment.repository;

import com.ham.netnovel.member.dto.MemberCommentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReCommentSearchRepository {



    List<MemberCommentDto> findReCommentByMember(String providerId, Pageable pageable);

}
