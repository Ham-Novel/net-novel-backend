package com.ham.netnovel.member.service;


import com.ham.netnovel.member.dto.MemberCoinUseHistoryDto;
import com.ham.netnovel.member.dto.MemberCommentDto;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;

import java.util.List;

public interface MemberMyPageService {


    List<MemberCommentDto> getMemberCommentAndReCommentList(String providerId);

    List<NovelFavoriteDto> getFavoriteNovelsByMember(String providerId);

//    List<FavoriteNovelListDto> getMemberFavoriteNovel


    List<MemberCoinUseHistoryDto> getMemberCoinUseHistory(String providerId);







}
