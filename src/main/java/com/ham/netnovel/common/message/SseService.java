package com.ham.netnovel.common.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {

    // 유저 ID를 키로, SseEmitter를 값으로 저장
    private Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    // 생성자 주입
    public SseService(Map<String, SseEmitter> sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    // 기본 생성자
    public SseService() {}

    // 새로운 SSE 연결 생성
    public SseEmitter createConnection(String providerId) {
        SseEmitter emitter = new SseEmitter();
        //유저 provider id로 새로운 emitter 생성
        sseEmitters.put(providerId, emitter);

        // 연결이 끊어졌을 때 처리
        emitter.onCompletion(() -> sseEmitters.remove(providerId));
        emitter.onTimeout(() -> sseEmitters.remove(providerId));

        return emitter;
    }

    /**
     * 특정 유저들에게 메시지를 전송합니다.
     *
     * <p>주어진 유저들의 `providerId` 목록을 기반으로, 연결된 `SseEmitter` 인스턴스에 메시지를 전송합니다.
     * 각 `providerId`에 대해 `SseEmitter`를 조회하고, 해당 `SseEmitter`가 유효한 경우에만 메시지를 전송합니다.
     * 만약 메시지 전송 중 오류가 발생하면 해당 `SseEmitter`를 연결 목록에서 제거합니다.
     * </p>
     *
     * @param memberProviderIds 메시지를 전송할 유저들의 `providerId` 목록입니다. 각 유저의 `SseEmitter`에 메시지를 전송합니다.
     * @param message           전송할 메시지 내용입니다. 모든 대상 유저에게 동일한 메시지가 전송됩니다.
     */
    public void sendMessageToMembers(List<String> memberProviderIds, Map<String, String> message) {
        log.info("유저에게 메시지 전달");
        for (String memberProviderId : memberProviderIds) {
            sendMessageToEmitter(memberProviderId, message);
        }
    }

    /**
     * 특정 클라이언트의 SSE 연결에 메시지를 전송합니다.
     *
     * <p>
     * 이 메서드는 주어진 `providerId`에 해당하는 클라이언트의 `SseEmitter`를 찾아서,
     * 지정된 메시지를 전송합니다. 메시지 전송 중 오류가 발생하면, 해당 클라이언트의 연결을 제거하고
     * 로그에 경고 메시지를 기록합니다.
     * </p>
     *
     * @param providerId 메시지를 전송할 대상 클라이언트의 식별자입니다. 이 식별자는 `sseEmitters` 맵에서
     *                   해당 클라이언트의 `SseEmitter`를 찾는 데 사용됩니다.
     * @param message 전송할 메시지를 포함하는 `Map` 객체입니다. 이 메시지는 SSE를 통해 클라이언트에게 전달됩니다.
     *                메시지는 `Map<String, String>` 형태로 전달되며, 클라이언트 측에서 이를 적절히 처리해야 합니다.
     */
    private void sendMessageToEmitter(String providerId, Map<String, String> message) {
        SseEmitter emitter = sseEmitters.get(providerId);
        if (emitter != null) {
            try {
                log.info("유저 정보={}", providerId);
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (IOException e) {
                log.warn("메시지 전송실패, providerId: {} 연결을 제거합니다.", providerId, e);
                sseEmitters.remove(providerId);
            }
        }
    }
    /**
     * 모든 활성화된 SSE 연결에 메시지를 전송합니다.
     *
     * <p>
     * 이 메서드는 내부적으로 유지 관리되는 모든 `SseEmitter` 객체에 대해 지정된 메시지를 전송합니다.
     * 각 `SseEmitter`는 클라이언트와의 SSE 연결을 나타내며, 이를 통해 실시간으로 데이터를 전송할 수 있습니다.
     *  </p>
     * <p>
     *
     * </p>만약 메시지 전송 중 오류가 발생하면, 해당 클라이언트의 `SseEmitter`를 제거하고 로그에 경고 메시지를 기록합니다.
     *
     *
     * @param message 전송할 메시지의 내용입니다. 이 메시지는 SSE를 통해 모든 연결된 클라이언트에게 전송됩니다.
     */
    public void sendMessageToAll(String message) {
//         모든 활성화된 SSE 연결에 메시지 전송
        sseEmitters.forEach((providerId, emitter) -> {
                    try {
                        emitter.send(SseEmitter.event().name("message").data(message));
                    } catch (IOException e) {
                        log.warn("Failed to send message to all. Removing providerId: {}", providerId, e);
                        sseEmitters.remove(providerId); // 오류가 발생하면 해당 유저의 SSE 연결 제거
                    }
                }
        );
    }




}
