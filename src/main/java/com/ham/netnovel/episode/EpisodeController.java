package com.ham.netnovel.episode;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.exception.EpisodeNotPurchasedException;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import com.ham.netnovel.episode.dto.EpisodeListInfoDto;
import com.ham.netnovel.episode.dto.EpisodeListItemDto;
import com.ham.netnovel.episode.service.EpisodeManagementService;
import com.ham.netnovel.episode.service.EpisodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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



    @GetMapping("/episodes/{episodeId}")
    public ResponseEntity<?> getEpisodeDetail(
            Authentication authentication,
            @PathVariable Long episodeId
    ) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        try {

            EpisodeDetailDto episodeDetail = episodeManagementService.getEpisodeDetail(principal.getName(), episodeId);
            return ResponseEntity.ok(episodeDetail);
        } catch (EpisodeNotPurchasedException e) {
            //실패 시 해당하는 코인 정책 반환
            HashMap<String, Integer> respBody = new HashMap<>() {{
                put("coinCost", e.getCoinCost());
            }};
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(respBody);
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

        if (sortBy.equals("recent")) {
            return ResponseEntity.ok(episodeService.getNovelEpisodesByRecent(novelId, pageable));
        } else if (sortBy.equals("initial")) {
            return ResponseEntity.ok(episodeService.getNovelEpisodesByInitial(novelId, pageable));
        } else {
            //정렬 값이 없으면 예외 발생
            throw new IllegalArgumentException("getEpisodesByNovel: invalid sortBy option");
        }
    }



    @GetMapping("/novels/{novelId}/episodes/info")
    public ResponseEntity<EpisodeListInfoDto> getEpisodesCountByNovel(
            @PathVariable(name = "novelId") Long novelId
    ) {
        return ResponseEntity.ok(episodeService.getNovelEpisodesInfo(novelId));
    }
}
