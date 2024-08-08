package com.ac.su;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable()); // CSRF 비활성화
        http.authorizeHttpRequests((authorize) -> authorize
                // 작동이 안되서 없앴더니 권한 적용 잘 작동함(게시판)
//                .requestMatchers("/board/1/posts").permitAll() // 자유 게시판은 모든 사용자 접근 허용
//                .requestMatchers("/club/{clubid}/board/2/posts").hasRole("CLUB_PRESIDENT") // 공지 게시판은 동아리장만 접근 허용
//                .requestMatchers("/board/3/club/{clubid}/posts").hasRole("CLUB_PRESIDENT") // 활동 게시판은 동아리장만 접근 허용
//                .requestMatchers("/club/{clubid}/board/4/posts").hasAnyRole("CLUB_MEMBER", "CLUB_PRESIDENT") // 커뮤니티내 자유 게시판은 동아리 회원과 동아리장만 접근 허용
                .anyRequest().permitAll() // 기타 모든 요청은 허용
        );
        // 폼 로그인 설정
        http.formLogin((formLogin) -> formLogin.loginPage("/login") // 폼으로 로그인, 로그인 페이지 URL 적어주기 GET
                .usernameParameter("studentId")
                .passwordParameter("password")
                .defaultSuccessUrl("https://zmffjq.store/") // HTTPS로 리디렉션 설정
        );
        // 로그아웃 설정
        http.logout((logout) -> logout
                .logoutUrl("/logout") // 로그아웃 URL
                .logoutSuccessUrl("https://zmffjq.store/") // 로그아웃 성공 시 이동할 URL
        );
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "https://plzget.github.io",
                                "https://zmffjq.store",
                                "https://www.zmffjq.store",
                                "http://zmffjq.store",
                                "http://www.zmffjq.store"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
