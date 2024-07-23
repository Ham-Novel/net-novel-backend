package com.ham.netnovel.member.service;


import com.ham.netnovel.member.dto.MemberCommentDto;

import java.util.List;

public interface MemberMyPageService {


    List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId);



}
