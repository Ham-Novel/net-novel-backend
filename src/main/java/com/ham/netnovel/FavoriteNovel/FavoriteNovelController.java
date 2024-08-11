package com.ham.netnovel.favoriteNovel;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.favoriteNovel.service.FavoriteNovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
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
     * @param authentication 유저 인증 보안
     * @param novelId 작품 PK id
     * @return ResponseEntity true, false 리턴
     */
    @GetMapping("/members/me/favorites/check")
    public ResponseEntity<Boolean> checkIfFavorite(
            Authentication authentication,
            @RequestParam(name = "novelId") Long novelId
    ) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 좋아요 누른 소설 반환
        Boolean isFavorite = favoriteNovelService.checkFavorite(principal.getName(), novelId);
//        Boolean isFavorite = favoriteNovelService.checkFavorite("test1", novelId);
        return ResponseEntity.ok(isFavorite);
    }

    /**
     * 해당 작품의 선호작 여부를 바꾸는 api
     * @param authentication 유저 인증 보안
     * @param novelId 작품 PK id
     * @return ResponseEntity true, false 리턴
     */
    @PostMapping("/members/me/favorites/{novelId}")
    public ResponseEntity<Boolean> toggleFavorite(
            Authentication authentication,
            @PathVariable(name = "novelId") Long novelId
    ) {
        //유저 인증 정보가 없으면 badRequest 응답, 정보가 있으면  CustomOAuth2User로 타입캐스팅
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저가 좋아요 누른 소설 반환
        Boolean isFavorite = favoriteNovelService.toggleFavoriteNovel(principal.getName(), novelId);
//        Boolean isFavorite = favoriteNovelService.toggleFavoriteNovel("test1", novelId);
        return ResponseEntity.ok(isFavorite);
    }
}
