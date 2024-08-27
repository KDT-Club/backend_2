package com.ac.su.config;

// 이 클래스의 목적은 Redis와의 연결을 설정하는 것임
// LettuceConnectionFactory는 Redis 서버와의 연결을 관리하며, 이 객체가 생성될 때 Redis 서버의 호스트와 포트 정보를 사용함
// 이 설정을 통해 Spring Boot 애플리케이션이 Redis 서버에 연결할 수 있게됨

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration // 이 클래스가 Spring의 설정 클래스로 사용됨을 나타냄
public class RedisConfig {
    // application.properties 또는 application.yml 파일에서 설정한 값을 주입받음
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean // Spring 컨테이너에 이 메서드의 반환 객체를 Bean으로 등록함
    public LettuceConnectionFactory redisConnectionFactory() {
        // RedisStandaloneConfiguration 객체를 생성하여 Redis 서버의 호스트와 포트 정보를 설정
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);

        // LettuceConnectionFactory는 Redis 서버와의 연결을 관리하는 객체로,
        // Spring에서 Redis를 사용할 때 연결을 생성하고 관리하는 데 사용됨
        return new LettuceConnectionFactory(redisConfig);
    }
}