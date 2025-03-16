package com.onestack.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// CSRF 보호 기능을 비활성화 (REST API 서버의 경우 일반적으로 비활성화)
				.csrf(csrf -> csrf.disable())

				// HTTP 요청에 대한 접근 권한 설정
				.authorizeHttpRequests(auth -> auth
						// 로그인 및 특정 경로는 접근 허용
						.requestMatchers("/login/**", "/loginForm/**", "/chat/**", "/estimation/**", "/**").permitAll()
						// 그 외 모든 요청은 인증된 사용자만 접근 가능
						.anyRequest().authenticated()
				);

		// ✅ OAuth2 관련 코드 주석 처리
		/*
		.oauth2Login(oauth2 -> oauth2
			// 커스텀 로그인 페이지 경로 설정
			.loginPage("/loginForm")
			// 로그인 성공 시 실행될 핸들러 설정
			.successHandler((request, response, authentication) -> {
				// 인증 성공 후 처리
				OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
				String email = oauth2User.getAttribute("email");

				// 로그 추가
				log.info("OAuth2 Login Success. Email: {}", email);

				// 컨트롤러로 OAuth2User 전달
				request.setAttribute("oauth2User", oauth2User);
				request.getRequestDispatcher("/google/callback").forward(request, response);
			})
			.failureHandler((request, response, exception) -> {
				// 인증 실패 시 처리
				log.error("OAuth2 Login Failed: {}", exception.getMessage());

				// 컨트롤러의 매핑된 URL로 포워드
				request.getRequestDispatcher("/loginForm?error=true").forward(request, response);
			})
			.userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
		);
		*/

		return http.build();  // 설정이 완료된 SecurityFilterChain 객체 반환
	}

	// ✅ OAuth2 사용자 서비스 주석 처리
	/*
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		// 기본 OAuth2 사용자 서비스 객체 생성
		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

		// OAuth2 사용자 정보를 로드하는 로직
		return (userRequest) -> {
			// 기본 서비스를 통해 OAuth2 사용자 정보를 가져옴
			OAuth2User oauth2User = delegate.loadUser(userRequest);
			return oauth2User;
		};
	}
	*/
}
