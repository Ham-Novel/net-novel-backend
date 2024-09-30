package com.ham.netnovel.novelRating;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.ValidationErrorHandler;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import com.ham.netnovel.novelRating.service.NovelRatingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@Slf4j
public class NovelRatingController {

    private final NovelRatingService novelRatingService;

    private final Authenticator authenticator;

    public NovelRatingController(NovelRatingService novelRatingService, Authenticator authenticator) {
        this.novelRatingService = novelRatingService;
        this.authenticator = authenticator;
    }

    @PostMapping
    public ResponseEntity<?> saveEpisodeRatingRequest(@Valid @RequestBody NovelRatingSaveDto novelRatingSaveDto,
                                                      BindingResult bindingResult,
                                                      Authentication authentication){
        //CommentUpdateDto Validation 에러가 있을경우 badRequest 전송
        if (bindingResult.hasErrors()) {
            log.error("saveEpisodeRatingRequest API 에러발생 ={}", bindingResult.getFieldError());
            //에러 메시지들 List에 담음
            List<String> errorMessages = ValidationErrorHandler.handleValidationErrors(bindingResult);
            //에러 메시지 body에 담아 전송
            return ResponseEntity.badRequest().body(String.join(", ", errorMessages));
        }

        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        novelRatingSaveDto.setProviderId(principal.getName());

        novelRatingService.saveNovelRating(novelRatingSaveDto);

        return ResponseEntity.ok("별점 등록 성공!");


    }




}
