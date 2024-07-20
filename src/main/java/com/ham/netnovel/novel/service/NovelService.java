package com.ham.netnovel.novel.service;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.novel.Novel;

import java.util.List;
import java.util.Optional;

public interface NovelService {

    List<Novel> getAllNovels();

    Optional<Novel> getNovel(Long id);

    Novel createNovel(Novel novel);

    void updateNovel(Long id, Novel novelDetails, Member updater);

    void deleteNovel(Long id);
}
