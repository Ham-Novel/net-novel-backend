package com.ham.netnovel.favoriteNovel;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.favoriteNovel.service.FavoriteNovelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "FavoriteNovel", description = "구독한 소설 관련 작업")
public class FavoriteNovelController {

    private final Authenticator authenticator;
    private final FavoriteNovelService favoriteNovelService;

    @Autowired
    public FavoriteNovelController(FavoriteNovelService favoriteNovelService, Authenticator authenticator) {
        this.favoriteNovelService = favoriteNovelService;
        this.authenticator = authenticator;
    }

    /**
     * 해당 작품이 선호작이 되어 있는지 확인하는 api
     *
     * @param authentication 유저 인증 보안
     * @param novelId        작품 PK id
     * @return ResponseEntity true, false 리턴
     */
    @Operation(summary = "선호작 여부 확인", description = "해당 소설이 사용자의 선호작으로 설정되어 있는지 확인합니다.",
            parameters = {
                    @Parameter(name = "novelId", description = "소설의 ID", example = "123", required = true)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 선호작 여부를 반환(true:이미등록된경우, false:등록되지 않은경우) ",
                    content = @Content(mediaType = "application/json",  schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Unauthorized", "message": "로그인 정보가 없습니다.", "status": 401 }
                                    """)))
    })
    @GetMapping("/members/me/favorites/check")
    public ResponseEntity<?> checkIfFavorite(
            Authentication authentication,
            @RequestParam(name = "novelId") Long novelId
    ) {

        if (authentication == null) {
            return ResponseEntity.ok("로그인 정보 없음");
        }

        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 좋아요 누른 소설 반환
        Boolean isFavorite = favoriteNovelService.checkFavorite(principal.getName(), novelId);
//        Boolean isFavorite = favoriteNovelService.checkFavorite("test1", novelId);
        return ResponseEntity.ok(isFavorite);
    }

    /**
     * 해당 작품의 선호작 여부를 바꾸는 api
     *
     * @param authentication 유저 인증 보안
     * @param novelId        작품 PK id
     * @return ResponseEntity true, false 리턴
     */

    @Operation(summary = "선호작 여부 변경", description = "해당 소설의 선호작 상태를 변경합니다. 이미 선호작품에 등록된경우, DB에서 삭제합니다.",
            parameters = {
                    @Parameter(name = "novelId", description = "소설의 ID", example = "123", required = true)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 선호작 상태를 변경하고 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = """
                                        { "error": "Unauthorized", "message": "로그인 정보가 없습니다.", "status": 401 }
                                    """)))})
    @PostMapping("/members/me/favorites/{novelId}")
    public ResponseEntity<Boolean> toggleFavorite(
            Authentication authentication,
            @PathVariable(name = "novelId") Long novelId
    ) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 좋아요 누른 소설 반환
        Boolean isFavorite = favoriteNovelService.toggleFavoriteNovel(principal.getName(), novelId);
        return ResponseEntity.ok(isFavorite);
    }
}
