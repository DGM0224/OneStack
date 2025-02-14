package com.onestack.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KakaoConfig {
    
//    @Value("${kakao.client.id}")
//    private String clientId;
//
//    @Value("${kakao.redirect.uri}")
//    private String redirectUri;
//
//    @Bean(name = "kakao.auth.url")
//    public String kakaoAuthUrl() {
//        return String.format("https://kauth.kakao.com/oauth/authorize"
//                + "?response_type=code"
//                + "&client_id=%s"
//                + "&redirect_uri=%s"
//                + "&scope=profile_nickname,profile_image"
//                + "&prompt=login consent",
//                clientId, redirectUri);
//    }
} 