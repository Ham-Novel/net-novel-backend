package com.ham.netnovel.config;

import com.ham.netnovel.OAuth.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final CustomOAuthUserService customOAuthUserService;

    public SecurityConfig(CustomOAuthUserService customOAuthUserService) {
        this.customOAuthUserService = customOAuthUserService;
    }

    //서블릿 필터 무시 URL 정적 리소스 등록 필수
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**","/favicon**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

         //CSRF disable, 테스트 상태에서만 disable 상태로 유지
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin((login) -> login.disable());

        //oauth2 방식 로그인사용
        http.oauth2Login(oauth2Login->
                        oauth2Login
                                .loginPage("/login")
                                .userInfoEndpoint(userInfoEndPoint ->
                                        userInfoEndPoint.userService(customOAuthUserService))
        );


        //사용자 인증 URL 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/","/login**").permitAll()//인증 예외 URL
                .anyRequest().authenticated()
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
