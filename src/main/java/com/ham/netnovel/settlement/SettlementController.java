package com.ham.netnovel.settlement;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.coinUseHistory.dto.NovelRevenueDto;
import com.ham.netnovel.common.utils.Authenticator;
import com.ham.netnovel.common.utils.PageableUtil;
import com.ham.netnovel.settlement.dto.SettlementHistoryDto;
import com.ham.netnovel.settlement.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * <p>
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

        //결과에 따라 메시지 전송
        if (result) {
            return ResponseEntity.ok("정산 신청 성공!");
        } else {
            return ResponseEntity.badRequest().body("이미 요청된 정산내역이 있습니다.");
        }

    }


    /**
     * 사용자의 정산 내역을 페이지네이션하여 반환하는 API 입니다.
     *
     * <p>
     * 페이지 번호와 페이지 크기를 쿼리 파라미터로 받아 사용자의 정산 내역을 가져오며,
     * 페이지 크기는 최대 100으로 제한됩니다. 인증 정보가 유효하지 않은 경우 예외가 발생합니다.
     * </p>
     *
     * @param pageNumber 페이지 번호 (기본값: 0)
     * @param pageSize 페이지 크기 (기본값: 10, 최대 100)
     * @param authentication 사용자 인증 정보
     * @return ResponseEntity {@link SettlementHistoryDto} 리스트로 사용자의 정산 내역
     */
    @GetMapping("/api/settlements/history")
    public ResponseEntity<?> getSettlementHistory(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            Authentication authentication) {
        //유저 인증정보 검증
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);

        //페이지 사이즈 수 제한
        if (pageSize > 100) {
            pageSize = 100;
        }
        //페이지네이션 객체 생성
        Pageable pageable = PageableUtil.createPageable(pageNumber, pageSize);

        //유저의 정산 내역을 가져옴
        List<SettlementHistoryDto> settlementHistory = settlementService.getSettlementHistory(principal.getName(), pageable);

        //정산 내역 반환
        return ResponseEntity.ok(settlementHistory);

    }


}
