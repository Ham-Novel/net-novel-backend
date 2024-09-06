package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
     * @return 생성한 episode id 값
     */
    Long createNovel(NovelCreateDto novelCreateDto);

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

    //ToDo 메서드 기능 통합 후 삭제
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
     * 주어진 기간에 따라 소설의 랭킹을 조회하여 해당 페이지의 소설 정보를 반환합니다.
     *
     * <p>이 메서드는 다음과 같은 작업을 수행합니다:</p>
     * <ul>
     *     <li>페이지 번호와 페이지 크기를 사용하여 데이터의 시작 인덱스와 끝 인덱스를 계산합니다.</li>
     *     <li>Redis에서 주어진 기간에 해당하는 소설 랭킹 데이터를 가져옵니다.</li>
     *     <li>가져온 데이터에서 소설 ID를 추출합니다.</li>
     *     <li>추출한 소설 ID를 사용하여 소설 엔티티를 조회하고, 랭킹 순서로 정렬하여 DTO로 변환합니다.</li>
     * </ul>
     *
     * <p>작업 중 예외가 발생하면 {@link ServiceMethodException}이 발생합니다.</p>
     *
     * @param period 소설 랭킹을 조회할 기간. "daily", "weekly", "monthly" 중 하나로 지정합니다.
     * @param pageable 페이지 정보. 페이지 번호와 페이지 크기를 포함합니다.
     * @return 주어진 페이지와 기간에 해당하는 소설 정보를 담은 {@link List}입니다.
     * @throws ServiceMethodException 메서드 실행 중 오류가 발생한 경우 발생합니다.
     */
    List<NovelListDto> getNovelsByRanking(String period, Pageable pageable);


    /**
     * AWS S3에 소설의 섬네일을 업로드하고, 소설 엔티티의 섬네일 정보를 업데이트합니다.
     *
     * <p>요청자의 정보와 소설 작가 정보가 일치하지 않거나, 소설이 데이터베이스에 없으면 {@link IllegalArgumentException}이 발생합니다.</p>
     *
     * <p>업로드와 저장이 성공하면 {@code true}를 반환합니다. 과정 중 오류가 발생하면 {@link ServiceMethodException}이 발생합니다.</p>
     *
     * @param file 소설의 섬네일로 업로드할 {@link MultipartFile} 타입 객체입니다.. {@code null}이 될 수 없습니다.
     * @param novelId 섬네일을 업데이트할 소설의 ID입니다. {@code null}이 될 수 없습니다.
     * @param providerId 섬네일 변경 요청자의 사용자 ID. {@code null}이 될 수 없습니다.
     * @return 섬네일 변경 성공 시 {@code true}. 실패 시 {@link ServiceMethodException}이 발생합니다.
     * @throws IllegalArgumentException 소설을 찾을 수 없거나 요청자와 소설 작가가 일치하지 않을 때 발생합니다.
     * @throws ServiceMethodException 파일 업로드나 소설 엔티티 저장 중 오류 발생 시 발생합니다.
     */
    boolean updateNovelThumbnail(MultipartFile file, Long novelId, java.lang.String providerId);


    /**
     * 지정된 페이지 범위 내에서 소설의 총 조회수를 반환하는 메서드입니다.
     *
     * <p>
     * 데이터베이스에서 소설의 ID와 해당 소설의 총 조회수를 조회한 후,
     * 이를 Map 형태로 반환합니다. Map의 키는 소설 ID(Long)이고, 값은 총 조회수(Long)입니다.
     * </p>
     *
     * @param pageable 페이징 정보를 담고 있는 {@link Pageable} 객체
     * @return 소설 ID({@link Long})를 키로, 총 조회수({@link Long})를 값으로 가지는 {@link Map} 객체
     * @throws ServiceMethodException 데이터 조회 중 예외 발생 시 해당 예외를 래핑하여 던집니다.
     */

    Map<Long,Long> getNovelWithTotalViews(Pageable pageable);

    /**
     * 지정된 페이지 범위 내에서 소설의 총 좋아요수를  반환하는 메서드입니다.
     *
     * <p>
     * 데이터베이스에서 소설의 ID와 해당 소설의 총 좋아요수를 조회한 후,
     * 이를 Map 형태로 반환합니다. Map의 키는 소설 ID(Long)이고, 값은 총 좋아요수(Integer)입니다.
     * </p>
     *
     * @param pageable 페이징 정보를 담고 있는 {@link Pageable} 객체
     * @return 소설 ID({@link Long})를 키로, 총 좋아요수를 ({@link Integer})를 값으로 가지는 {@link Map} 객체
     * @throws ServiceMethodException 데이터 조회 중 예외 발생 시 해당 예외를 래핑하여 던집니다.
     */
    Map<Long,Integer> getNovelWithTotalFavorites(Pageable pageable);


    /**
     * 지정된 페이지 범위 내에서 소설의 최근 업데이트 날짜를 반환하는 메서드입니다.
     *
     * <p>
     * 데이터베이스에서 소설의 ID와 해당 소설의 최근 업데이트 날짜를 조회한 후,
     * 이를 Map 형태로 반환합니다.
     * </p>
     * <p>
     * Map의 키는 소설 ID(Long)이고, 값은최근 업데이트 날짜(LocalDateTime)입니다.
     * </p>
     *
     * @param pageable 페이징 정보를 담고 있는 {@link Pageable} 객체
     * @return 소설 ID ({@link Long})를 키로, 최근 업데이트 날짜 ({@link LocalDateTime})를 값으로 가지는 {@link Map} 객체
     * @throws ServiceMethodException 데이터 조회 중 예외 발생 시 해당 예외를 래핑하여 던집니다.
     */
    Map<Long, LocalDateTime> getNovelWithLatestEpisodeCreateTime(Pageable pageable);


    /**
     * 주어진 정렬 기준과 페이지 정보를 바탕으로 소설 목록을 조회하는 메서드입니다.
     *
     * <p>
     * 이 메서드는 기본적으로 소설 목록을 조회수 기준으로 정렬합니다.
     * </p>
     * <p>
     * 사용자가 제공한 정렬 기준에 따라, 소설 목록을 좋아요 순 또는 최신순으로 정렬할 수 있습니다.
     * 조회된 소설 목록의 썸네일 URL은 S3 서비스의 CloudFront URL로 변환됩니다.
     * </p>
     * @param sortOrder 소설 목록의 정렬 기준을 나타내는 문자열입니다.
     *                  "favorites"는 좋아요 순, "latest"는 최신순, 기본값은 "view"로 조회수 순입니다.
     * @param pageable 페이지 정보 및 페이징 조건을 포함하는 {@link Pageable} 객체입니다.
     *
     * @return List<NovelListDto> 소설 목록을 포함한 DTO 리스트를 반환합니다.
     *         각 소설의 썸네일 URL은 CloudFront URL로 변환되어 반환됩니다.
     */
    List<NovelListDto> getNovelsBySearchCondition(String sortOrder, Pageable pageable, List<Long> tagIds);

}
