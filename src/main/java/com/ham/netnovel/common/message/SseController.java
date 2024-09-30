package com.ham.netnovel.common.message;

import com.ham.netnovel.common.OAuth.CustomOAuth2User;
import com.ham.netnovel.common.utils.Authenticator;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    private final SseService sseService;
    private final Authenticator authenticator;

    public SseController(SseService sseService, Authenticator authenticator) {
        this.sseService = sseService;
        this.authenticator = authenticator;
    }

    /**
     * 클라이언트의 SSE 연결을 수립합니다.
     *
     * <p>
     * 이 메서드는 클라이언트의 SSE 연결을 생성하여 실시간으로 서버의 이벤트를 수신할 수 있도록 합니다.
     * 클라이언트의 인증 정보를 기반으로 `SseEmitter` 객체를 생성하고 반환합니다.
     * </p>
     * <p>
     * 인증된 사용자의 이름을 사용하여 `SseEmitter`를 생성하며, 이 `SseEmitter`를 통해 실시간 알림이나 업데이트를 전송할 수 있습니다.
     * </p>
     *
     * @param authentication 클라이언트의 인증 정보를 포함하는 {@link Authentication} 객체입니다.
     *                       이 객체를 사용하여 클라이언트의 인증 상태를 확인하고 사용자 정보를 얻습니다.
     * @return `SseEmitter` {@link SseEmitter}객체를 반환합니다. 이 객체는 클라이언트와의 SSE 연결을 나타내며, 서버에서 발생하는 이벤트를 실시간으로 클라이언트에게 전달합니다.
     */

    @GetMapping("/api/sse/subscribe")
    public SseEmitter createSseConnection(Authentication authentication) {
        CustomOAuth2User principal = authenticator.checkAuthenticate(authentication);
        return sseService.createConnection(principal.getName());
//        return sseService.createConnection("test1");
    }
}
