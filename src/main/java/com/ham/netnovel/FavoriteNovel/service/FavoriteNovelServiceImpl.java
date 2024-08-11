package com.ham.netnovel.favoriteNovel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.favoriteNovel.FavoriteNovel;
import com.ham.netnovel.favoriteNovel.FavoriteNovelId;
import com.ham.netnovel.favoriteNovel.FavoriteNovelRepository;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class FavoriteNovelServiceImpl implements FavoriteNovelService{

    private final FavoriteNovelRepository favoriteNovelRepository;
    private final MemberService memberService;
    private final NovelService novelService;

    @Autowired
    public FavoriteNovelServiceImpl(FavoriteNovelRepository favoriteNovelRepository, MemberService memberService, NovelService novelService) {
        this.favoriteNovelRepository = favoriteNovelRepository;
        this.memberService = memberService;
        this.novelService = novelService;
    }

    @Override
    @Transactional
    public Boolean toggleFavoriteNovel(String providerId, Long novelId) {
        //유저, 작품 레코드 DB 검증
        Member member = memberService.getMember(providerId)
                .orElseThrow(() -> new NoSuchElementException("toggleFavoriteNovel() Error : 존재하지 않는 Member 입니다."));
        Novel novel = novelService.getNovel(novelId)
                .orElseThrow(() -> new NoSuchElementException("toggleFavoriteNovel() Error : 존재하지 않는 Novel 입니다."));

        try {
            FavoriteNovelId id = new FavoriteNovelId(member.getId(), novel.getId());
            Optional<FavoriteNovel> record = favoriteNovelRepository.findById(id);


            //이미 레코드가 있으면 삭제
            if (record.isPresent()) {
                favoriteNovelRepository.delete(record.get());
                return false; // 이제 레코드 없음
            }
            //레코드가 없으면 새로 생성
            else {
                FavoriteNovel newRecord = FavoriteNovel.builder()
                        .id(id)
                        .member(member)
                        .novel(novel)
                        .build();
                FavoriteNovel save = favoriteNovelRepository.save(newRecord);
                return true; // 이제 레코드 있음
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("toggleFavoriteNovel() Error : "  + ex.getMessage());
        }
    }

    @Override
    public Boolean checkFavorite(String providerId, Long novelId) {
        //유저, 작품 레코드 DB 검증
        Member member = memberService.getMember(providerId)
                .orElseThrow(() -> new NoSuchElementException("toggleFavoriteNovel() Error : 존재하지 않는 Member 입니다."));
        Novel novel = novelService.getNovel(novelId)
                .orElseThrow(() -> new NoSuchElementException("toggleFavoriteNovel() Error : 존재하지 않는 Novel 입니다."));

        try {
            FavoriteNovelId id = new FavoriteNovelId(member.getId(), novel.getId());
            Optional<FavoriteNovel> record = favoriteNovelRepository.findById(id);
            return record.isPresent();
        } catch (Exception ex) {
            throw new ServiceMethodException("checkFavorite() Error : "  + ex.getMessage());
        }
    }
}
