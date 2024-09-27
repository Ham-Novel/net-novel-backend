package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    List<NovelInfoDto> getNovelsByAuthor(String providerId);


 /**
  * 유저가 작성한 소설의 ID 목록을 반환하는 메서드 입니다.
  *
  * <p>유효한 providerId가 제공되면, 해당 작가가
  * 작성한 모든 소설의 ID를 포함하는 {@link List}를 반환합니다.</p>
  *
  * @param providerId 유저 정보
  * @return 해당 작가가 작성한 소설의 ID {@link List} 객체
  * @throws IllegalArgumentException providerId가 null이거나 비어 있는 경우
  */
   List<Long> getNovelIdsByAuthor(String providerId);





    /**
     * 주어진 소설 ID를 사용하여 소설의 상태를 삭제상태로 변경하는 메서드 입니다.
     *
     *
     * <p>
     * 소설 ID 로 소설을 조회합니다.
     * 소설 작가와 삭제 요청자의 providerId 가 동일한경우 소설을 DELETED_BY_USER 로 변경하고 DB에 저장합니다.
     * </p>
     * @param novelDeleteDto 소설 삭제에 필요한 정보가 담긴 DTO
     * @throws NoSuchElementException 해당 ID의 소설이 존재하지 않을 경우 발생
     * @throws AccessDeniedException 접근 권한이 없을 경우 발생
     * @throws ServiceMethodException 권한 검증 도중 예외가 발생했을 경우 발생
     */
    void deleteNovel(NovelDeleteDto novelDeleteDto);

    /**
     * 주어진 소설 ID를 사용하여 소설 정보를 조회합니다.
     * <p>
     * 이 메서드는 제공된 소설 ID를 통해 DB에서 소설 데이터를 조회합니다.
     * 만약 DB에 소설 정보가 없으면{@link NoSuchElementException} 로 예외를 던집니다.
     *</p>
     *
     * @param novelId 조회할 소설의 ID
     * @return 조회된 소설의 정보가 담긴 {@link NovelInfoDto}
     * @throws NoSuchElementException 소설 정보를 찾을 수 없을 때 발생
     * @throws ServiceMethodException 메서드 실행 중 변환 과정에서 예외가 발생할 때 발생
     */
    NovelInfoDto getNovelInfo(Long novelId);



    /**
     * 별점 점수가 있는 Novel의 id값들을 List로 반환하는 메서드
     * @return List Long 타입으로 반환
     */
    List<Long> getRatedNovelIds();


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






}
