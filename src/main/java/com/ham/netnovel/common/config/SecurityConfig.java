package com.ham.netnovel.common.config;

import com.ham.netnovel.common.OAuth.CustomOAuth2SuccessHandler;
import com.ham.netnovel.common.OAuth.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final CustomOAuthUserService customOAuthUserService;

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(CustomOAuthUserService customOAuthUserService, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.customOAuthUserService = customOAuthUserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    //서블릿 필터 무시 URL 정적 리소스 등록 필수
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //CSRF disable, 테스트 상태에서만 disable 상태로 유지
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin((login) -> login.disable());

        //oauth2 방식 로그인사용
        http.oauth2Login(oauth2Login -> oauth2Login
                .loginPage("/login")
                .userInfoEndpoint(userInfoEndPoint ->
                        userInfoEndPoint.userService(customOAuthUserService))
                .successHandler(customOAuth2SuccessHandler)
        );


        //사용자 인증 URL 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login**", "/api/**", "/api/novels/**", "/v3/**", "/swagger-ui/**")
                .permitAll() // Swagger 관련 경로 추가
                .anyRequest().authenticated()
        );


        return http.build();
    }


}
