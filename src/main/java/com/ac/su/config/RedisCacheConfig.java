package com.ac.su.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration // 이 클래스가 Spring의 설정 클래스로 사용됨을 나타냄
@EnableCaching // Spring의 캐시 기능을 활성화함
public class RedisCacheConfig {
    @Bean
    public CacheManager boardCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // RedisCacheConfiguration 객체를 생성하여 Redis 캐시의 기본 설정을 정의함
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                // Redis에 저장할 키를 String 형태로 직렬화하여 저장하도록 설정
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                // Redis에 저장할 값을 JSON 형태로 직렬화하여 저장하도록 설정
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<Object>(Object.class)
                        )
                )
                // 캐시에 저장된 데이터의 만료 시간을 1분으로 설정함
                .entryTtl(Duration.ofMinutes(1L));

        // RedisCacheManager는 캐시를 관리하는 객체로, 여기서 RedisConnectionFactory를 사용해
        // Redis 서버에 연결하고, 위에서 정의한 RedisCacheConfiguration을 기본 설정으로 사용함
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
