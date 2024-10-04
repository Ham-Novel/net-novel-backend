package com.ham.netnovel.novel.repository;

import com.ham.netnovel.novel.Novel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelSearchRepository {


    @Query("select n from Novel n " +
            "join fetch n.author m " +
            "left join fetch n.novelMetaData nm " +
            "left join fetch n.novelAverageRating nr " +
            "where m.providerId =:providerId " +
            "and n.status = 'ACTIVE'")
    List<Novel> findNovelsByMember(@Param("providerId") String providerId);



    @Query("select n from Novel n " +
            "where n.id in " +
            "(select fn.novel.id from FavoriteNovel fn " +
            "where fn.member.providerId =:providerId)")
    List<Novel> findFavoriteNovelsByMember(@Param("providerId") String providerId);




    @Query("select distinct n " +
            "from Novel n " +
            "where n in " +
            "(select r.novel from NovelRating r " +
            "where r.rating is not null)")
    List<Novel> findByNovelRating();


    /**
     * 최신 에피소드의 생성일을 기준으로 내림차순 정렬된 소설을 반환하는 메서드 입니다.
     *
     * @param pageable 페이지네이션 정보를 포함하는 객체
     * @return 최신 에피소드가 포함된 소설 목록
     */
    @Query("select n " +
            "from Novel n " +
            "join n.episodes e " + // Novel 엔티티와 Episode 엔티티를 조인합니다.
            "WHERE e.createdAt = (" +
            "   SELECT MAX(e2.createdAt) " + // 각 소설의 에피소드 중 가장 최근 생성된 시간 선택
            "   FROM Episode e2" +
            "   WHERE e2.novel = n" + // 현재 소설(n)와 연관된 에피소드들 중에서 선택
            ") " +
            "order by e.createdAt desc") // 최신 에피소드의 생성일로 내림차순 정렬
    List<Novel> findByLatestEpisodes(Pageable pageable);


    /**
     * Novel 엔티티의 ID 값들로, 엔티티를 찾아 List로 반환하는 메서드
     * @param novelIds Novel 엔티티 ID 값을 담는 List 객체
     * @return List<Novel>
     */
    @Query("select n from Novel n " +
            "left join fetch n.novelAverageRating " +//novelAverageRating이 Novel과 관계가 없어도, Novel 엔티티 반환
            "where n.id in :novelIds")
    List<Novel> findByNovelIds(@Param("novelIds") List<Long> novelIds);


    /**
     * 특정 페이지 범위 내에서 소설(novel)의 id와, 총 조회수를 반환합니다.
     *
     * @param pageable 페이지 정보를 포함한 {@ling Pageable}Pageable 객체
     * @return 소설 ID와 해당 소설의 총 조회수(totalViews)를 포함한 리스트.
     *         첫번째 요소는 소설의 id, 두번째 요소는 소설의 총 조회수
     */
    //Todo having절 오버헤드 감소방법
    @Query("select n.id, " +
            "sum (e.view) as totalViews " +//조회수 합산
            "from Novel n " +
            "join n.episodes e " +//에피소드와 조인
            "group by n " +
            "having sum(e.view) >0 " +//에피소드가 있는 소설만 선택
            "order by n.id ")//소설별로 그룹화
    List<Object[]> findNovelTotalViews(Pageable pageable);

    /**
     * 특정 페이지 범위 내에서 소설(novel)의 id와 좋아요수를 반환합니다.
     *
     * @param pageable 페이지 정보를 포함한 {@ling Pageable}Pageable 객체
     * @return 소설 ID와 해당 소설의 총 조회수(totalViews)를 포함한 리스트.
     *         첫번째 요소는 소설의 id, 두번째 요소는 소설의 총 좋아요 수
     */
    @Query("select n.id, " +
            "count (f.id) as totalFavorites " +//Favorites 수 계산
            "from Novel n " +
            "join n.favorites f " +//Favorites 와 조인
            "group by n " +//소설별로 그룹화
            "having count(f) >0 " +//좋아요 기록이 있는 소설만 반환
            "order by n.id ")//소설 id로 정렬
    List<Object[]> findNovelTotalFavorite(Pageable pageable);


    /**
     * 특정 페이지 범위 내에서 소설(novel)의 id와 최근 업데이트 날짜를 반환합니다..
     *
     * @param pageable 페이지 정보를 포함한 {@ling Pageable}Pageable 객체
     * @return 소설 ID와 해당 소설의 총 조회수(totalViews)를 포함한 리스트.
     *         첫번째 요소는 소설의 id, 두번째 요소는 소설의 최근 업데이트 날짜
     */

    @Query("select n.id," +
            "max(e.createdAt) " +
            "from Novel n " +
            "join n.episodes e " +
            "group by n " +
            "order by n.id")
    List<Object[]> findNovelLatestUpdatedEpisode(Pageable pageable);






}
