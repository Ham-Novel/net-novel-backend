package com.ham.netnovel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //        //CSRF disable, 테스트 상태에서만 disable 상태로 유지
        http.csrf(AbstractHttpConfigurer::disable);

        //경로설정
        // 명시적으로 모든 요청 허용
        http.authorizeHttpRequests(authorize ->
                authorize
                        .anyRequest().permitAll()
        );
        return http.build();
    }


















//
//    /*
//    서블릿 필터 무시하는 메서드
//    정적 리소스 서블릿 필터 적용 제외
//     */
////    @Bean
////    public WebSecurityCustomizer webSecurityCustomizer() {
////        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
////    }
//
//
//    /*
//    서블릿 필터 설정
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        //세션설정 : STATELESS (JWT 사용)
//        http.sessionManagement((session) -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        //CSRF disable, 테스트 상태에서만 disable 상태로 유지
//        http.csrf((csrf) -> csrf.disable());
//
//        //form 로그인 방식 disable(OAuth 소셜 로그인 사용)
//        http.formLogin((login) -> login.disable());
//
//        http.authorizeHttpRequests((authorize) ->
//                authorize.requestMatchers("/**").permitAll()
//        );
////
////        http.authorizeRequests(authorizeRequests ->
////                authorizeRequests
////                        //사용자 인증 없이 접속 가능한 URI 설정
////                        .requestMatchers(
////                                "/",
////                                "/home/**",
////                                "/login/**",
////                                "/error",
////                                "/test/**",
////                                "/images/**"
////                        ).permitAll()
////                        .anyRequest().authenticated()
////        );
//
//
//
//        return http.build();
//
//
//    }
//
//


    }
