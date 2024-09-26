package com.ham.netnovel.settlement;

import com.ham.netnovel.OAuth.CustomOAuth2User;
import com.ham.netnovel.coinUseHistory.dto.NovelRevenueDto;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.settlement.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SettlementController {


    private final SettlementService settlementService;

    private final Authenticator authenticator;

    @Autowired
    public SettlementController(SettlementService settlementService, Authenticator authenticator) {
        this.settlementService = settlementService;
        this.authenticator = authenticator;
    }


    /**
     * 유저가 자신이 작성한 소설에 대한 수익 정보(벌어들인 코인, 벌어들인 금액)를 기간별로 전달하는 API입니다.
     * <p>이 API는 유저의 인증 정보를 검증한 후,
     * 해당 유저가 작성한 소설의 수익 정보를 조회하여 반환합니다. </p>
     *
     * <p>성공적으로 처리된 경우, 상태 코드 200 OK와 함께 {@link List<NovelRevenueDto>} 형태의
     * 정산 정보를 반환합니다.</p>
     *
     * @param authentication 유저의 인증 정보를 포함한 객체
     * @return ResponseEntity<?> 유저의 정산 정보를 담고 있는 리스트를 포함한 HTTP 응답
     */
    @GetMapping("/api/settlements")
    public ResponseEntity<?> getRevenueByAuthor(
            Authentication authentication) {

        //유저 인증정보 검증
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //유저 정보로 수익 정보를 받아옴
        List<NovelRevenueDto> revenueByAuthor = settlementService.getNovelRevenueByAuthor(principal.getName());

        //정보 전송
        return ResponseEntity.ok(revenueByAuthor);
    }

    /**
     * 정산 요청을 처리하는 API입니다.
     *
     * <p>
     * 유저가 자신이 작성한 소설의 수익에 대한 정산을 요청할 경우, 정산 요청을 생성합니다.

     * 정산 정보 생성에 성공한 경우, HTTP 200 OK와 함께 성공 메시지를 전송합니다.
     * 만약 이미 요청 중인 정산 정보가 있다면, HTTP 400 Bad Request와 함께 에러 메시지를 반환합니다.
     * </p>
     *
     * @param authentication 유저의 인증정보
     * @return ResponseEntity<?> 요청 처리 결과를 포함하는 HTTP 응답 {@link ResponseEntity} 객체.
     */
    @PostMapping("/api/settlements")
    public ResponseEntity<?> createSettlement(Authentication authentication) {

        //유저 인증정보 검증
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        boolean result = settlementService.processSettlementRequest(principal.getName());

        if (result) {
            return ResponseEntity.ok("정산 신청 성공!");
        } else {
            return ResponseEntity.badRequest().body("이미 요청된 정산내역이 있습니다.");
        }

    }


}
