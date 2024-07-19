package com.ham.netnovel.novel.service;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.Service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NovelServiceImpl implements NovelService {
    private final NovelRepository novelRepository;

    @Autowired
    public NovelServiceImpl(NovelRepository novelRepository, MemberService memberService) {
        this.novelRepository = novelRepository;
    }

    @Override
    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @Override
    public Optional<Novel> getNovel(Long id) {
        return novelRepository.findById(id);
    }

    @Override
    public Novel createNovel(Novel novel) {
        return novelRepository.save(novel);
    }

    @Override
    public void updateNovel(Long id, Novel novelDetails, Member updater) {
        //예외 처리
        try {
            //변경할 Novel 존재 검증
            Novel novel = novelRepository.findById(id).orElseThrow();

            //Novel 변경 권한 검증
            if (!novel.getAuthorId().equals(updater.getProviderId())) {
                throw new RuntimeException("작가 본인만 작품 수정이 가능합니다.");
            }

            //변경사항 novelDetails 내용 일치 검증
            if (novel.equals(novelDetails)) {
                throw new RuntimeException("변경 사항이 없습니다.");
            }

            // Logic
            novel.setTitle(novelDetails.getTitle());
            novel.setDescription(novelDetails.getDescription());
            novel.setStatus(novelDetails.getStatus());

            // JPA save() 메소드는 자동으로 변경 감지 => create(X) update(O) 작업 수행
            novelRepository.save(novel);

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteNovel(Long id) {
        Optional<Novel> novelToDelete = novelRepository.findById(id);
        novelRepository.delete(novelToDelete.orElseThrow());
    }
}
