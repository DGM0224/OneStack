package com.onestack.project.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.onestack.project.domain.Member;
import com.onestack.project.service.MemberService;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/oauth/kakao")
public class KakaoController {

//    private static final Logger log = LoggerFactory.getLogger(KakaoController.class);
//
//    @Value("${kakao.client.id}")
//    private String CLIENT_ID;
//
//    @Value("${kakao.redirect.uri}")
//    private String REDIRECT_URI;
//
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping("/callback")
//    public String kakaoCallback(
//        @RequestParam(name = "code", required = true) String code,
//        @RequestParam(name = "error", required = false) String error,
//        @RequestParam(name = "error_description", required = false) String errorDescription,
//        HttpSession session,
//        Model model) {
//
//        log.info("=== Kakao Callback Start ===");
//        log.info("REDIRECT_URI: {}", REDIRECT_URI);
//
//        // 에러가 있는 경우 처리
//        if (error != null) {
//            log.error("Kakao auth error: {}, description: {}", error, errorDescription);
//            return "redirect:/loginForm?error=kakao";
//        }
//
//        try {
//            // 1. 액세스 토큰 받기
//            String accessToken = requestAccessToken(code);
//            session.setAttribute("accessToken", accessToken);
//            log.info("Access token received: {}", accessToken);
//
//            // 2. 사용자 정보 받기
//            Map<String, Object> userData = requestUserInfo(accessToken);
//            log.info("User info received: {}", userData);
//
//            // 3. 카카오 ID 추출
//            String kakaoId = userData.get("id").toString();
//            Map<String, Object> properties = (Map<String, Object>) userData.get("properties");
//            String nickname = (String) properties.get("nickname");
//            String profileImage = (String) properties.get("profile_image");
//
//            // 4. 기존 회원인지 확인
//            Member existingMember = memberService.getMember(kakaoId);
//            log.info("Existing member check - kakaoId: {}, exists: {}", kakaoId, existingMember != null);
//
//            if (existingMember != null) {
//                // 5a. 기존 회원이면 로그인 처리
//                log.info("Existing member found. Logging in...");
//                session.setAttribute("member", existingMember);
//                session.setAttribute("isLogin", true);
//
//                Member memberPro = (Member) session.getAttribute("member");
//                if (memberPro != null) {
//                    int memberNo = memberPro.getMemberNo();
//                    int proNo = memberService.getProNo(memberNo);
//
//                    if (proNo > 0) {
//                        session.setAttribute("proNo", proNo);
//                    }
//                }
//
//
//                return "redirect:/mainPage";
//            } else {
//                // 5b. 신규 회원이면 추가 정보 입력 페이지로
//                log.info("New member. Redirecting to additional info page...");
//                model.addAttribute("kakaoId", kakaoId);
//                model.addAttribute("nickname", nickname);
//                model.addAttribute("profileImage", profileImage);
//                return "member/kakaoAddJoinForm";
//            }
//        } catch (Exception e) {
//            log.error("Error in Kakao callback: ", e);
//            return "redirect:/loginForm?error=kakao";
//        }
//    }
//
//    @GetMapping("/logout")
//    public String kakaoLogout(HttpSession session) {
//        try {
//            // 1. 세션에서 저장된 액세스 토큰 가져오기
//            String accessToken = (String) session.getAttribute("accessToken");
//
//            if (accessToken != null) {
//                // 2. Kakao 로그아웃 API 호출
//                RestTemplate restTemplate = new RestTemplate();
//                HttpHeaders headers = new HttpHeaders();
//                headers.setBearerAuth(accessToken);
//
//                HttpEntity<Void> request = new HttpEntity<>(headers);
//                ResponseEntity<String> response = restTemplate.exchange(
//                    "https://kapi.kakao.com/v1/user/logout",
//                    HttpMethod.POST,
//                    request,
//                    String.class
//                );
//
//                log.info("Kakao logout response: {}", response.getBody());
//            } else {
//                log.warn("No access token found in session");
//            }
//
//            // 3. 서버 세션 무효화
//            session.invalidate();
//            log.info("User session invalidated");
//
//            return "redirect:/loginForm?logout=true";
//        } catch (Exception e) {
//            log.error("Error during Kakao logout", e);
//            return "redirect:/loginForm?error=logout";
//        }
//    }
//
//    @PostMapping("/register")
//    public String register(Member member, HttpSession session) {
//
//        try {
//            log.info("Registering new member with kakao ID: {}", member.getMemberId());
//
//            // 1. 비밀번호 암호화
//            String temporaryPassword = member.getMemberId() + UUID.randomUUID().toString().substring(0, 8);
//            String encodedPassword = passwordEncoder.encode(temporaryPassword);
//
//            // 2. Member 객체  설정
//            member.setPass(encodedPassword);
//            member.setSocial(true);
//            member.setSocialType("kakao");
//            member.setMemberType(0);
//
//            log.info("Registering social member: {}", member);
//            memberService.insertMember(member);
//
//            // 4. 세션에 회원 정보 저장
//            session.setAttribute("member", member);
//            session.setAttribute("isLogin", true);
//
//
//            return "redirect:/mainPage";
//
//        } catch (Exception e) {
//            log.error("카카오 멤버에서 에러 확인: {}", e.getMessage(), e);
//            return "redirect:/loginForm?error=register";
//        }
//    }
//
//    private String requestAccessToken(String code) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//        parameters.add("grant_type", "authorization_code");
//        parameters.add("client_id", CLIENT_ID);
//        parameters.add("redirect_uri", REDIRECT_URI);
//        parameters.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
//
//        try {
//            log.info("Token Request Parameters: {}", parameters);
//
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                "https://kauth.kakao.com/oauth/token",
//                request,
//                String.class
//            );
//
//            log.info("Token Response Status: {}", response.getStatusCode());
//            log.info("Token Response Body: {}", response.getBody());
//
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);
//
//                if (responseMap.containsKey("access_token")) {
//                    return (String) responseMap.get("access_token");
//                } else {
//                    log.error("Access token not found in response");
//                    throw new RuntimeException("Failed to get access token");
//                }
//            } catch (JsonProcessingException e) {
//                log.error("Error parsing JSON response", e);
//                throw new RuntimeException("Error parsing token response", e);
//            }
//        } catch (Exception e) {
//            log.error("Token request error", e);
//            throw new RuntimeException("Failed to get access token", e);
//        }
//    }
//
//    private Map<String, Object> requestUserInfo(String accessToken) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        try {
//            log.info("Requesting user info with token: {}", accessToken);
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    "https://kapi.kakao.com/v2/user/me",
//                    HttpMethod.GET,
//                    request,
//                    Map.class
//            );
//            log.info("User info received: {}", response.getBody());
//            return response.getBody();
//        } catch (Exception e) {
//            log.error("Error requesting user info", e);
//            throw e;
//        }
//    }
//
//    @GetMapping("/additionalInfo")
//    public String showAdditionalInfoForm(Model model) {
//        return "member/additionalInfo";
//    }
//
//    @GetMapping("/login")
//    public String kakaoLogin() {
//        String kakaoAuthUrl = String.format(
//            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=profile_nickname,profile_image&prompt=login consent",
//            CLIENT_ID, REDIRECT_URI
//        );
//        return "redirect:" + kakaoAuthUrl;
//    }
}
