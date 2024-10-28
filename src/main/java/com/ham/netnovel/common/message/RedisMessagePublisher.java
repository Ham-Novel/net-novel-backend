package com.ham.netnovel.common.message;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 * Redis를 사용하여 메시지를 발행하는 컴포넌트 클래스입니다.
 * <p>
 * 이 클래스는 Redis의 Publish/Subscribe 기능을 사용하여 특정 채널에 메시지를 전송합니다.
 * 이를 통해 Redis를 구독하고 있는 클라이언트들에게 메시지를 실시간으로 전달할 수 있습니다.
 * </p>
 */
@Component
public class RedisMessagePublisher {

    // RedisTemplate을 사용하여 Redis와 상호작용
    private final RedisTemplate<String, String> redisTemplate;


    /**
     * RedisMessagePublisher 생성자
     *
     * @param redisTemplate Redis 서버와의 상호작용을 위한 RedisTemplate 객체
     */
    @Autowired
    public RedisMessagePublisher(
            @Qualifier("redisCacheTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 주어진 채널에 메시지를 발행합니다.
     *
     * <p>이 메서드는 Redis를 사용하여 특정 채널에 메시지를 전송합니다.
     * Redis의 publish/subscribe 기능을 활용하여 메시지를 구독하고 있는 클라이언트에게 전송됩니다.
     * </p>
     *
     * @param channel 발행할 Redis 채널의 이름
     * @param message 발행할 메시지의 내용
     */
    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
