package com.ham.netnovel.novel;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import com.ham.netnovel.novel.data.NovelSearchType;
import com.ham.netnovel.novel.dto.*;
import com.ham.netnovel.novel.service.NovelCRUDService;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class NovelController {
    private final NovelService novelService;
    private final NovelCRUDService novelCRUDService;
    private final Authenticator authenticator;

    public NovelController(NovelService novelService, NovelCRUDService novelCRUDService, Authenticator authenticator) {
        this.novelService = novelService;
        this.novelCRUDService = novelCRUDService;
        this.authenticator = authenticator;
    }

    /**
     * 소설 상세 페이지에서 Novel 데이터 응답하는 API
     *
     * @param novelId novelId를 담은 url path variable
     * @return ResponseEntity
     */
    @GetMapping("/novels/{novelId}")
    public ResponseEntity<NovelInfoDto> getNovel(@PathVariable("novelId") Long novelId) {
        return ResponseEntity.ok(novelService.getNovelInfo(novelId));
    }

//    @GetMapping
//    public ResponseEntity<List<NovelInfoDto>> getNovelList() {
//        return null;
//    }


    /**
     * 소설을 검색 조건에 따라 조회하는 API 입니다.
     *
     * <p>
     * 이 메서드는 유저가 제공한 정렬 기준, 페이지 번호, 페이지 크기에 따라 소설 목록을 반환합니다.
     * 페이지 네이션을 위해 Pageable 객체를 생성하여, 서비스 계층에서 소설 정보를 DTO 리스트로 받아 이를 HTTP 응답으로 전송합니다.
     * </p>
     *
     * @param sortBy     정렬 기준을 나타내는 {@link String} 객체입니다.. 기본값은 "view"입니다. (예: "view","favorites" 등)
     * @param pageNumber 조회할 페이지 번호 {@link Integer} 객체 입니다. 기본값은 0입니다.
     * @param pageSize   한 페이지에 포함될 항목의 수 {@link Integer} 객체  입니다. 기본값은 100입니다.
     * @return {@link ResponseEntity<>} 소설 정보가 포함된 리스트를 HTTP 200 응답으로 반환합니다.
     * 응답 본문에는 소설 정보 리스트가 담겨 있습니다.
     */
    @GetMapping("/novels/browse")
    public ResponseEntity<List<NovelListDto>> getNovelsBySearchCondition(
            @RequestParam(name = "sortBy", defaultValue = "view") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(name = "tagId", required = false) String ids) {

        List<Long> idList = new ArrayList<>();
        //"," 로 구분된 tag 들을 분리하여 List 객체에 담음
        if (!(ids == null)) {
            idList = Arrays.stream(ids.split(","))
                    .map(Long::valueOf)
                    .toList();
        }
        //페이지 사이즈 수 제한
        if (pageSize > 200) {
            pageSize = 200;
        }

        //페이지 네이션을 위한 Pageable 객체 생성
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //조건을 메서드에 전달하여, 소설 정보 List를 받아옴
        List<NovelListDto> novels = novelService.getNovelsBySearchCondition(sortBy, pageable, idList);
        return ResponseEntity.ok(novels);//소설 정보 전송
    }

    /**
     * 검색어로 소설을 조회하는 API 입니다.
     * <p>이 메서드는 유저가 입력한 검색어에 따라 소설제목 또는 작가명으로 소설을 검색하여
     * 소설 목록을 반환합니다.</p>
     * <p>페이지 네이션을 위해 Pageable 객체를 생성하여,
     * 서비스 계층에서 소설 정보를 DTO 리스트로 받아 이를 HTTP 응답으로 전송합니다.</p>
     *
     * @param searchWord 검색어 {@link String} 객체입니다.
     * @param searchType 검색 타입 {@link String} 객체입니다. 기본값은 소설 제목 입니다.
     * @param pageNumber 조회할 페이지 번호 {@link Integer} 객체 입니다. 기본값은 0입니다.
     * @param pageSize   한 페이지에 포함될 항목의 수 {@link Integer} 객체  입니다. 기본값은 30입니다.
     * @return {@link ResponseEntity<>} 소설 정보가 포함된 리스트를 HTTP 200 응답으로 반환합니다.
     */
    @GetMapping("/novels/search")
    public ResponseEntity<?> getNovelsBySearchWord(
            @RequestParam(name = "searchWord") String searchWord,
            @RequestParam(name = "searchType",defaultValue = "title") String searchType,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "30") Integer pageSize) {
        String trimWord = searchWord.trim();
        // 앞뒤 공백 제거 후 2글자 미만일 경우 BAD REQUEST 반환
        if (trimWord.length() < 2) {
            return ResponseEntity.badRequest().body("검색어는 2글자 이상 입력해주세요!");
        }
        //페이지 사이즈 수 제한
        if (pageSize > 200) {
            pageSize = 200;
        }

        // 클라이언트가 선택한 seachType을 enum 상수로 변환, 디폴트값은 소설제목 검색
        NovelSearchType novelSearchType;
        switch (searchType){
            case "author" -> novelSearchType =NovelSearchType.AUTHOR_NAME;
            default ->novelSearchType= NovelSearchType.NOVEL_TITLE ;
        }
        //페이지 네이션을 위한 Pageable 객체 생성
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //조건을 메서드에 전달하여, 소설 정보 List를 받아옴
        List<NovelListDto> novels = novelService.getNovelsBySearchWord(trimWord, novelSearchType,pageable);
        return ResponseEntity.ok(novels);//소설 정보 전송
    }


    /**
     * 소설 랭킹을 기간에 따라 조회하는 API 엔드포인트입니다.
     * <p>
     * 사용자가 제공한 기간과 페이지 정보를 바탕으로 소설 목록을 랭킹 순서대로 정렬하여 반환합니다.
     * </p>
     * <p>
     * 페이지 네이션을 위해 Pageable 객체를 생성하고, 소설 서비스에서 요청된 랭킹 기간에 해당하는 소설 정보를 가져와
     * HTTP 응답으로 전송합니다.
     * </p>
     *
     * @param period     소설 랭킹을 조회할 기간을 나타내는 {@link String} 객체입니다.. (예: "weekly", "monthly" 등)
     * @param pageNumber 조회할 페이지 번호입니다. 기본값은 0입니다.
     * @param pageSize   한 페이지에 포함될 항목의 수입니다. 기본값은 100입니다.
     * @return {@link ResponseEntity<> } 소설 정보가 포함된 랭킹 순서의 리스트를 HTTP 200 응답으로 반환합니다.
     * 응답 본문에는 랭킹이 반영된 소설 목록이 담겨 있습니다.
     */
    @GetMapping("/novels/ranking")
    public ResponseEntity<List<NovelListDto>> getNovelsByRanking(
            @RequestParam("period") String period,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "100") int pageSize) {


        //페이지 사이즈 수 제한
        if (pageSize > 200) {
            pageSize = 200;
        }

        //페이지네이션 객체 생성
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //유저가 요청한 랭킹 기간에 따라, 소설 정보를 랭킹 순서대로 정렬하여 List에 담음
        List<NovelListDto> rankedNovels = novelService.getNovelsByRanking(period, pageable);


        return ResponseEntity.ok(rankedNovels);//소설 정보 전송
    }

    /**
     * 유저가 집핍하는 소설 리스트를 전송하는 API
     *
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity 유저가 소유한 소설 리스트를 담은 응답 객체
     */
    @GetMapping("/members/me/novels")
    public ResponseEntity<?> getMyWorks(Authentication authentication) {

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        List<NovelInfoDto> novels = novelService.getNovelsByAuthor(principal.getName());

        //정보 전송
        return ResponseEntity.ok(novels);

    }


    /**
     * 유저가 생성한 소설(Novel) 서버에 저장하는 API
     *
     * @param reqBody        Novel 엔티티의 title, desc, providerId(유저 정보)를 담은 객체
     * @param bindingResult  DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity HttpStatus, Novel 데이터 문자열 반환.
     */
    @PostMapping("/novels")
    public ResponseEntity<?> createNovel(@Valid @RequestBody NovelCreateDto reqBody,
                                         BindingResult bindingResult,
                                         Authentication authentication) {
        //NovelCreateDto Validation 예외 처리
        if (bindingResult.hasErrors()) {
            log.error("createNovel API Error = {}", bindingResult.getFieldError());
            //badRequest 에러 메세지 body에 담아 전송
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증. 없으면 badRequest 응답. 있으면 CustomOAuth2User 타입캐스팅.
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보(providerId) 값 저장
        reqBody.setAccessorProviderId(principal.getName());

        return ResponseEntity.ok(novelCRUDService.createNovel(reqBody));
    }

    /**
     * 유저가 소설(Novel)의 변경된 내용을 업데이트하는 API
     *
     * @param reqBody        novelId, title, desc, providerId를 담은 객체
     * @param bindingResult  DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity HttpStatus, Novel 데이터 문자열 반환.
     */
    @PutMapping("/novels/{novelId}")
    public ResponseEntity<String> updateNovel(@PathVariable("novelId") Long urlNovelId,
                                              @Valid @RequestBody NovelUpdateDto reqBody,
                                              BindingResult bindingResult,
                                              Authentication authentication) {
        //NovelUpdateDto Validation 예외 처리
        if (bindingResult.hasErrors()) {
            log.error("updateNovel API Error = {}", bindingResult.getFieldError());
            //badRequest 에러 메세지 body에 담아 전송
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증. 없으면 badRequest 응답. 있으면 CustomOAuth2User 타입캐스팅.
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보(providerId) 값 저장
        reqBody.setAccessorProviderId(principal.getName());

        return ResponseEntity.ok("작품 업데이트 완료.");
    }

    /**
     * 유저가 생성한 소설을 DELETE_BY_USER로 삭제 처리하는 API
     *
     * @param reqBody        novelId, providerId를 담은 객체
     * @param bindingResult  DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity HttpStatus, Novel 데이터 문자열 반환.
     */
    @DeleteMapping("/novels/{novelId}")
    public ResponseEntity<String> deleteNovel(@PathVariable("novelId") Long urlNovelId,
                                              @Valid @RequestBody NovelDeleteDto reqBody,
                                              BindingResult bindingResult,
                                              Authentication authentication) {
        //NovelDeleteDto Validation 예외 처리
        if (bindingResult.hasErrors()) {
            log.error("deleteNovel API Error = {}", bindingResult.getFieldError());
            //badRequest 에러 메세지 body에 담아 전송
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저 인증. 없으면 badRequest 응답. 있으면 CustomOAuth2User 타입캐스팅.
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        //DTO에 유저 정보(providerId) 값 저장
        reqBody.setAccessorProviderId(principal.getName());

        return ResponseEntity.ok("작품 삭제 완료.");
    }

    @GetMapping("/novels")
    public ResponseEntity<List<NovelInfoDto>> getNovelsBySorted(@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        return ResponseEntity.ok(novelService.getNovelsRecent(pageable));
    }

    /**
     * 소설의 섬네일을 AWS S3에 업로드하고, 소설의 섬네일 파일명을 업데이트하는 API입니다.
     *
     * <p>클라이언트로부터 소설 ID와 섬네일 파일을 받아서 인증된 사용자의 정보를 기반으로 소설의 섬네일을 업데이트합니다.</p>
     *
     * <p>사용자는 인증이 필요하며, {@link Authentication} 객체를 통해 인증 정보를 확인합니다.
     * 인증 정보가 일치하지 않으면 {@link AuthenticationCredentialsNotFoundException}이 발생합니다.</p>
     *
     * <p>인증된 사용자의 정보가 소설의 소유자와 일치하면, 소설의 섬네일 파일을 S3에 업로드하고,
     * 소설 엔티티의 섬네일 파일명을 업데이트합니다.</p>
     *
     * <p>업데이트가 성공하면 HTTP 200 상태 코드와 "섬네일 변경 성공!" 메시지를 반환하며,
     * 실패할 경우 HTTP 500 상태 코드와 "섬네일 변경 실패." 메시지를 반환합니다.</p>
     *
     * @param urlNovelId     섬네일을 업데이트할 소설의 ID입니다. {@code notNull}
     * @param multipartFile  업로드할 섬네일 파일입니다. {@code notNull}
     * @param authentication 현재 인증된 사용자의 인증 정보입니다. {@code notNull}
     * @return 성공 시 HTTP 200과 "섬네일 변경 성공!" 메시지, 실패 시 HTTP 500과 "섬네일 변경 실패." 메시지를 반환합니다.
     */
    @PostMapping("/novels/{novelId}/thumbnail")
    public ResponseEntity<?> uploadNovelThumbnail(
            @PathVariable("novelId") Long urlNovelId,
            @RequestParam("file") MultipartFile multipartFile, //업로드할 섬네일 파일
            Authentication authentication
    ) {
        //유저 인증. 없으면 badRequest 응답. 있으면 CustomOAuth2User 타입캐스팅.
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //섬네일 변경 후 결과를 넘겨받음
        boolean result = novelService.updateNovelThumbnail(multipartFile, urlNovelId, principal.getName());

        //결과가 true 일경우 성공 메시지 전송
        if (result) {
            return ResponseEntity.ok("섬네일 변경 성공!");


        } else {
            //결과가 true가 아닐경우 실패 메시지 전송
            return ResponseEntity.internalServerError().body("섬네일 변경 실패.");
        }


    }

}
