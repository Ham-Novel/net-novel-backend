package com.ham.netnovel.episode;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.exception.EpisodeNotPurchasedException;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import com.ham.netnovel.episode.data.IndexDirection;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import com.ham.netnovel.episode.dto.EpisodeListInfoDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.dto.*;
import com.ham.netnovel.episode.service.EpisodeManagementService;
import com.ham.netnovel.episode.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class EpisodeController {
    private final EpisodeService episodeService;
    private final EpisodeManagementService episodeManagementService;

    private final Authenticator authenticator;

    @Autowired
    public EpisodeController(EpisodeService episodeService, EpisodeManagementService episodeManagementService, Authenticator authenticator) {
        this.episodeService = episodeService;
        this.episodeManagementService = episodeManagementService;
        this.authenticator = authenticator;
    }

    /**
     * 특정 에피소드의 상세 정보를 반환하는 API 입니다.
     *
     * <p>에피소드가 무료인 경우, 에피소드 상세 정보가 직접 반환됩니다.</p>
     * <p>유료인 경우, 사용자가 인증된 상태인지 확인하고 결제 내역을 검증한 후 에피소드 정보를 반환합니다.</p>
     *
     * @param authentication 현재 사용자의 인증 정보
     * @param episodeId 조회할 에피소드의 ID
     * @return 에피소드 상세 정보를 담은 {@link ResponseEntity} 객체
     * @throws EpisodeNotPurchasedException 유료 에피소드를 구매하지 않은 경우, {@code 402 PAYMENT REQUIRED} 응답과 결제 정보 반환
     * @response 200 OK 에피소드 상세 정보가 성공적으로 조회된 경우
     * @response 401 UNAUTHORIZED 사용자가 인증되지 않은 경우
     * @response 402 PAYMENT REQUIRED 사용자가 에피소드를 구매하지 않은 경우
     */
    @GetMapping("/episodes/{episodeId}")
    public ResponseEntity<?> getEpisodeDetail(
            Authentication authentication,
            @PathVariable Long episodeId
    ) {

        //에피소드가 무료인지 확인, 무료일경우 true 유료일경우 false 반환
        boolean episodeFree = episodeService.isEpisodeFree(episodeId);

        //에피소드가 무료일경우 에피소드 데이터 전송
        if (episodeFree) {
            EpisodeDetailDto freeEpisodeDetail = episodeManagementService.getFreeEpisodeDetail(episodeId);
            return ResponseEntity.ok(freeEpisodeDetail);
        } else {
            //에피소드가 유료일경우, 로그인 정보 검증, 결제 내역 확인 후 에피소드 데이터 전송

            //유저 인증 정보가 없으면 401 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
            CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
            try {
                //유저 인증 정보가 있을경우, 에피소드 상세정보를 불러옴
                EpisodeDetailDto episodeDetail = episodeManagementService.getEpisodeDetail(principal.getName(), episodeId);
                //에피소드 정보 전송
                return ResponseEntity.ok(episodeDetail);
            } catch (EpisodeNotPurchasedException e) {
                //유저의 에피소드 결제 내역이 없을때 PAYMENT_REQUIRED 전송
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(e.getPaymentInfo());
            }
        }
    }

    @GetMapping("/episodes/{episodeId}/beside")
    public ResponseEntity<?> getEpisodeBeside(
            Authentication authentication,
            @PathVariable Long episodeId,
            @RequestParam(defaultValue = "NEXT") IndexDirection direction
    ) {
        //유저 인증 정보가 없으면 401 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        try {
            EpisodeDetailDto episodeDetail = episodeManagementService.getBesideEpisode(principal.getName(), episodeId, direction);
            return ResponseEntity.ok(episodeDetail);
        } catch (IndexOutOfBoundsException ignored) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (EpisodeNotPurchasedException e) {
            //실패 시 해당하는 코인 정책 반환
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(e.getPaymentInfo());
        }
    }

    @GetMapping("/novels/{novelId}/episodes")
    public ResponseEntity<List<EpisodeListItemDto>> getEpisodesByNovel(
            @PathVariable(name = "novelId") Long novelId,
            @RequestParam(name = "sortBy", defaultValue = "recent") String sortBy,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {

        //Pageable 객체 생성. null or 음수이면 예외 발생
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);
        //정렬 조건, 페이지네이션 정보로 에피소드 정보를 받아옴
        List<EpisodeListItemDto> novelByFilter = episodeService.getEpisodesByConditions(sortBy, novelId, pageable);
        //에피소드 정보 전송
        return ResponseEntity.ok(novelByFilter);

    }


    @GetMapping("/novels/{novelId}/episodes/info")
    public ResponseEntity<EpisodeListInfoDto> getEpisodesCountByNovel(
            @PathVariable(name = "novelId") Long novelId
    ) {
        return ResponseEntity.ok(episodeService.getNovelEpisodesInfo(novelId));
    }

    /**
     * 새로운 에피소드를 생성하는 API 입니다.
     *
     * <p>주어진 소설 ID와 에피소드 생성 정보를 바탕으로 새로운 에피소드를 생성합니다.
     * 에피소드 생성 요청이 유효한지 검증하고, 유효하지 않은 경우에는
     * BadRequest 응답을 반환합니다. 인증된 사용자의 정보는 {@link EpisodeCreateDto}에
     * 설정됩니다. 에피소드 생성이 성공하면  OK 응답을 반환합니다.</p>
     *
     * @param novelId          소설의  ID
     * @param episodeCreateDto 에피소드 생성 정보를 담고 있는 {@link EpisodeCreateDto}객체
     * @param bindingResult    검증 오류 정보를 담고 있는 {@link BindingResult}객체
     * @param authentication   현재 사용자의 인증 상태를 포함하는 인증 객체.
     * @return {@code ResponseEntity<String>} 에피소드 업데이트 결과를 담고 있는 응답 객체 반환
     */
    @PostMapping("/novels/{novelId}/episodes")
    public ResponseEntity<String> createEpisode(
            @PathVariable(name = "novelId") Long novelId,
            @Valid @RequestBody EpisodeCreateDto episodeCreateDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        //EpisodeCreateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrorMessages(
                    bindingResult,
                    "createEpisode");
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        //유저인증정보 체크
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        episodeCreateDto.setProviderId(principal.getName());//DTO에 유저 정보 할당
        episodeCreateDto.setNovelId(novelId);//DTO에 novelId 정보 할당

        //에피소드 생성
        episodeService.createEpisode(episodeCreateDto);
        //문제가 없을경우 OK 전송
        return ResponseEntity.ok("ok");
    }


    /**
     * 주어진 요청 본문에 포함된 세부 정보를 사용하여 기존 에피소드를 업데이트 하는 API 입니다.
     *
     * <p>이 메서드는 URL의 {@code episodeId} 경로 변수로 지정된 에피소드를 업데이트하는 요청을 처리합니다.
     * 먼저 인증 정보를 확인하여 요청자가 유효한지 검증합니다.
     * 요청 본문은 {@link EpisodeUpdateDto}를 사용하여 검증되며 문제가 있으면 BadRequest 응답을 반환합니다. </p>
     * <p>요청에 문제가 없을경우 에피소드을 업데이트 한 후  OK 응답을 반환합니다.</p>
     *
     * @param episodeId        업데이트할 에피소드의 ID.
     * @param episodeUpdateDto 에피소드의 새로운 세부 정보를 포함하는 {@link EpisodeUpdateDto} 객체
     * @param authentication   현재 사용자의 인증 상태를 포함하는 인증 객체.
     * @return {@code ResponseEntity<String>} 에피소드 업데이트 결과를 담고 있는 응답 객체 반환
     */

    @PostMapping("/episodes/{episodeId}")
    public ResponseEntity<String> updateEpisode(
            @PathVariable(name = "episodeId") Long episodeId,
            @Valid @RequestBody EpisodeUpdateDto episodeUpdateDto,
            Authentication authentication
    ) {

        //유저인증정보 체크
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        episodeUpdateDto.setEpisodeId(episodeId);//DTO에 novelId 정보 할당
        episodeUpdateDto.setProviderId(principal.getName());//DTO에 유저 정보 할당.

        //에피소드 업데이트
        episodeService.updateEpisode(episodeUpdateDto);

        return ResponseEntity.ok("ok");
    }


    /**
     * 에피소드를 삭제상태로 바꾸는 API 입니다.
     *
     * <p>주어진 에피소드 ID를 사용하여 에피소드를 삭제 상태로 변경합니다.
     * 먼저, 인증 정보를 확인하여 요청자가 유효한지 검증합니다.
     * 이후, {@link  EpisodeDeleteDto} 객체를 생성하여 에피소드 ID와
     * 인증된 사용자의 정보를 설정한 후, 에피소드 삭제 서비스 메서드를 호출합니다. </p>
     * <p>삭제가 성공하면 OK 응답을 반환합니다.</p>
     *
     * @param episodeId      삭제할 에피소드의 ID
     * @param authentication 현재 사용자의 인증 상태를 포함하는 인증 객체.
     * @return {@code ResponseEntity<String>} 에피소드 업데이트 결과를 담고 있는 응답 객체 반환
     */
    @DeleteMapping("/episodes/{episodeId}")
    public ResponseEntity<String> deleteEpisode(
            @PathVariable(name = "episodeId") Long episodeId,
            Authentication authentication
    ) {

        //유저인증정보 체크
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);


        //에피소드 ID 와 유저정보로 DTO 생성
        EpisodeDeleteDto dto = EpisodeDeleteDto.builder()
                .episodeId(episodeId)
                .providerId(principal.getName())
                .build();

        //에피소드를 삭제 상태로 변경
        episodeService.deleteEpisode(dto);

        return ResponseEntity.ok("ok");
    }
}
