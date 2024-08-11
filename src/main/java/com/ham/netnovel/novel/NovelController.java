package com.ham.netnovel.novel;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelDeleteDto;
import com.ham.netnovel.novel.dto.NovelInfoDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novel.service.NovelService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class NovelController {
    private final NovelService novelService;
    private final Authenticator authenticator;

    public NovelController(NovelService novelService, EpisodeService episodeService, Authenticator authenticator) {
        this.novelService = novelService;
        this.authenticator = authenticator;
    }

    /**
     * 소설 상세 페이지에서 Novel 데이터 응답하는 API
     * @param novelId novelId를 담은 url path variable
     * @return ResponseEntity
     */
    @GetMapping("/novels/{novelId}")
    public ResponseEntity<NovelInfoDto> getNovel(@PathVariable("novelId") Long novelId) {
        return ResponseEntity.ok(novelService.getNovelInfo(novelId));
    }

    @GetMapping
    public ResponseEntity<List<NovelInfoDto>> getNovelList() {
        return null;
    }

    /**
     * 주어진 랭킹 기간에 따라 소설 랭킹 정보를 조회하고 반환하는 API
     * @param period 쿼리 파라미터로 랭킹 기간을 받음, (daily, weekly, monthly, all-time 중 하나)
     * @return ResponseEntity<List<NovelInfoDto>> 소설 정보를 담은 객체 반환.
     */
    @GetMapping("/novels/ranking")
    public ResponseEntity<List<NovelInfoDto>> getNovelsByRanking(@RequestParam("period") String period){
        //유저가 요청한 랭킹 기간에 따라, 소설 정보를 랭킹 순서대로 정렬하여 List에 담음
        List<NovelInfoDto> rankedNovels = novelService.getNovelsByRanking(period);
        return ResponseEntity.ok(rankedNovels);//소설 정보 전송
    }


    /**
     * 유저가 생성한 소설(Novel) 서버에 저장하는 API
     * @param reqBody Novel 엔티티의 title, desc, providerId(유저 정보)를 담은 객체
     * @param bindingResult DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
     * @param authentication 유저의 인증 정보
     * @return ResponseEntity HttpStatus, Novel 데이터 문자열 반환.
     */
    @PostMapping
    public ResponseEntity<String> createNovel(@Valid @RequestBody NovelCreateDto reqBody,
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

        return ResponseEntity.ok("작품 생성 완료.");
    }

    /**
     * 유저가 소설(Novel)의 변경된 내용을 업데이트하는 API
     * @param reqBody novelId, title, desc, providerId를 담은 객체
     * @param bindingResult DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
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

        return ResponseEntity.ok("작품 업데이트 완료.");    }

    /**
     * 유저가 생성한 소설을 DELETE_BY_USER로 삭제 처리하는 API
     * @param reqBody novelId, providerId를 담은 객체
     * @param bindingResult DTO 유효성 검사 정보, 에러 발생시 객체에 에러가 담김
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
    public ResponseEntity<List<NovelInfoDto>> getNovelsBySorted(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        return ResponseEntity.ok(novelService.getNovelsRecent(pageable));
    }

}
