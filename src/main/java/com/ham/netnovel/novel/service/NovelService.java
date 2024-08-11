package com.ham.netnovel.novel.service;

import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelInfoDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NovelService {


    /**
     * novelId 값으로 DB에서 Novel 엔티티 반환. Null 체크 필요.
     * @param novelId Novel의 PK값
     * @return Optional<Novel>
     */
    Optional<Novel> getNovel(Long novelId);

    /**
     * providerId 값으로 DB에서 Novel 엔티티 리스트 반환. Null 체크 필요.
     * @param providerId 유저 id
     * @return Optional<Novel>
     */
    List<Novel> getNovelsByAuthor(String providerId);

    /**
     * 유저가 생성한 Novel을 DB 저장.
     * @param novelCreateDto 생성 정보가 담긴 dto
     */
    void createNovel(NovelCreateDto novelCreateDto);

    /**
     * DB에 저장된 Novel 데이터 변경.
     * @param novelUpdateDto 업데이트 정보가 담긴 dto
     */
    void updateNovel(NovelUpdateDto novelUpdateDto);

    /**
     * DB에 저장된 Novel 삭제.
     * @param novelDeleteDto 삭제 정보가 담긴 dto
     */
    void deleteNovel(NovelDeleteDto novelDeleteDto);

    /**
     * Novel 엔티티를 대략적으로 설명하는 데이터를 가져오는 메서드
     * @param novelId Novel PK 값
     * @return NovelInfoDto
     */
    NovelInfoDto getNovelInfo(Long novelId);

    /**
     * DB의 저장된 모든 Novel 리스트를 최신순으로 가져오는 메서드.
     * 페이지 단위로 일부 리스트만 가져온다.
     * @param pageable page number, page size
     * @return
     */
    List<NovelInfoDto> getNovelsRecent(Pageable pageable);

    /**
     * 유저의 선호작 Novel 리스트 반환.
     * @param providerId 유저 PK 값.
     * @return List<Novel>
     */
    List<Novel> getFavoriteNovels(String providerId);

    /**
     * 별점 점수가 있는 Novel의 id값들을 List로 반환하는 메서드
     * @return List Long 타입으로 반환
     */
    List<Long> getRatedNovelIds();


    /**
     *랭킹에 따른 소설 상세정보를 전달하는 메서드
     * @param period 랭킹주기(daily,weekly,monthly,all-time 디폴트값은 daily)
     * @return List<NovelInfoDto>
     */
    List<NovelInfoDto> getNovelsByRanking(String period);



}
