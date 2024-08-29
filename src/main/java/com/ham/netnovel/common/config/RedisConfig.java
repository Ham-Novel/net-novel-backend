package com.ham.netnovel.common.config;

import com.ham.netnovel.common.message.NovelUpdateMessageSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
/**
 * Redis 관련 설정을 구성하는 클래스입니다.
 * Redis 연결, RedisTemplate, 메시지 리스너 컨테이너 등을 설정합니다.
 */
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * Redis 서버와의 연결을 생성하는 Bean을 생성합니다.
     *
     * @return Redis 서버와의 연결을 관리하는 {@link RedisConnectionFactory} 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        //설정된 정보로 RedisConnectionFactory 생성
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * Redis 서버와 상호작용하기 위한 RedisTemplate Bean을 생성합니다.
     * 문자열 형태의 키와 값을 처리하도록 설정됩니다.
     *
     * @param redisConnectionFactory Redis 연결을 위한 {@link RedisConnectionFactory} 객체
     * @return Redis 서버와의 데이터 작업을 수행하는 {@link RedisTemplate} 객체
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // RedisConnectionFactory를 RedisTemplate에 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // Key와 Value에 대해 StringRedisSerializer를 사용하여 직렬화/역직렬화를 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));

        return redisTemplate;
    }

    /**
     * Redis Pub/Sub 메시지를 처리하기 위한 리스너 컨테이너를 생성합니다.
     * 지정된 채널에서 메시지를 수신하고, 해당 메시지를 처리할 리스너를 등록합니다.
     *
     * @param redisConnectionFactory Redis 연결을 위한 {@link RedisConnectionFactory} 객체
     * @param novelUpdateMessageSubscriber 수신될 메시지를 처리할 {@link NovelUpdateMessageSubscriber} 객체
     * @param novelUpdateTopic                  메시지를 수신할 {@link ChannelTopic} 객체
     * @return Redis 메시지 리스너를 관리하는 {@link RedisMessageListenerContainer} 객체
     */
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            NovelUpdateMessageSubscriber novelUpdateMessageSubscriber,
            ChannelTopic novelUpdateTopic) {

        // RedisMessageListenerContainer 객체 생성
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        // Redis 서버와의 연결을 설정
        container.setConnectionFactory(redisConnectionFactory);


        /*
        소설 업데이트 관련 메시지 설정
        지정된 채널(novelUpdateTopic)에서 수신한 메시지를 NovelUpdateMessageSubscriber로 전달
         */
        container.addMessageListener(novelUpdateMessageSubscriber, novelUpdateTopic);

        // 설정이 완료된 RedisMessageListenerContainer 객체를 반환
        return container;
    }
    /**
     * Redis Pub/Sub에서 사용될 채널 토픽을 생성합니다.
     *
     * @return 메시지를 발행하고 수신할 {@link ChannelTopic} 객체
     */
    @Bean
    public ChannelTopic novelUpdateTopic() {
        return new ChannelTopic("novel-update-channel");
    }


    ;
//
//    /**
//     * Redis 메시지를 redisMessageSubscriber 객체의 메소드 호출로 변환하는 어댑터
//     * <p>
//     * 메시지가 수신되면, 지정된 메서드를 호출
//     * </p>
//     *
//     * @param redisMessageSubscriber 메시지를 처리할 사용자 정의 클래스
//     * @return
//     */
//    @Bean
//    public MessageListenerAdapter messageListenerAdapter(NovelUpdateMessageSubscriber redisMessageSubscriber) {
//        return new MessageListenerAdapter(redisMessageSubscriber, "onMessage");//RedisMessageSubscriber의 메서드 이름
//    }


}


