package com.ham.netnovel.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
//                .allowedOrigins("http://localhost:5173")  // Vue.js 개발 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
