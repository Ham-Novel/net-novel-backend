package com.ham.netnovel.common.message;

import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.favoriteNovel.service.FavoriteNovelService;
import com.ham.netnovel.s3.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis에서 발행된 메시지를 수신하고 처리하는 클래스입니다.
 */
@Slf4j
@Component
public class NovelUpdateMessageSubscriber implements MessageListener {

    private final SseService sseService;

    private final FavoriteNovelService favoriteNovelService;

    private final S3Service s3Service;

    @Autowired
    public NovelUpdateMessageSubscriber(SseService sseService, FavoriteNovelService favoriteNovelService, S3Service s3Service) {
        this.sseService = sseService;
        this.favoriteNovelService = favoriteNovelService;
        this.s3Service = s3Service;
    }

    /**
     * Redis로부터 수신된 메시지를 처리하는 메서드입니다.
     *
     * @param message 수신된 Redis 메시지
     * @param pattern 수신된 채널의 패턴 (사용되지 않음)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String messageBody = new String(message.getBody());
            log.info("Received message: {} from channel: {}", messageBody, channel);


            // 메시지 포맷을 'episodeId:episodeContent'로 가정
            String[] parts = messageBody.split(":", 5);

            Map<String, String> messageMap = getStringStringMap(parts);

            Long novelId = TypeValidationUtil.validateLong(messageMap.get("novelId"));
            List<String> providerIds = favoriteNovelService.getSubscribedMemberProviderIds(novelId);

            //테스트로그
//            for (String providerId : providerIds) {
//                log.info("유저정보 ={}", providerId);
//                log.info("----------------------------------------");
//            }

            if (providerIds.isEmpty()) {
                log.warn("onMessage 경고, 소설에 좋아요를 누른 유저가 없습니다. novelId={}", novelId);
                return;
            }
            //좋아요 누른 유저가 있을경우, SSE 로 메시지 발송
            sseService.sendMessageToMembers(providerIds, messageMap);
            // 추가 로직 구현
        } catch (Exception ex) {
            log.error("Error processing message: {}", message, ex);
        }


    }


    private Map<String, String> getStringStringMap(String[] parts) {
        if (parts.length == 5) {
            String novelIdString = parts[0];
            String episodeIdString = parts[1];
            String novelTitle = "[업데이트]" + parts[2];
            String episodeTitle = parts[3];
            String cloudFrontUrl = s3Service.generateCloudFrontUrl(parts[4], "mini");

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("novelId", novelIdString);
            messageMap.put("episodeId", episodeIdString);
            messageMap.put("novelTitle", novelTitle);
            messageMap.put("episodeTitle", episodeTitle);
            messageMap.put("thumbnailUrl", cloudFrontUrl);
            return messageMap;

        } else {
            log.error("onMessage 에러, messageBody 형식이 올바르지 않습니다.");
            return null;
        }
    }

}
