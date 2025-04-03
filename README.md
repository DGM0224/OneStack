# 🛠 OneStack - IT 전문가 매칭 플랫폼

<p align="center">
  <img src="https://www.onestack.store/images/onestack.png" alt="OneStack Logo" width="200">
</p>

## 📌 프로젝트 소개

OneStack은 IT 전문가와 의뢰인을 연결하는 매칭 플랫폼입니다. 검증된 전문가 풀을 통해 신뢰성 있는 외주 서비스를 제공하며, 실시간 채팅과 프로젝트 관리 기능으로 효율적인 협업을 지원합니다.

### 🎯 핵심 가치
- **신뢰성**: 검증된 전문가 매칭 시스템
- **효율성**: 실시간 소통 및 프로젝트 관리
- **투명성**: 명확한 가격 정책과 리뷰 시스템

## 🛠 기술 스택

### Frontend
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![jQuery](https://img.shields.io/badge/jQuery-0769AD?style=flat-square&logo=jquery&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=flat-square&logo=mybatis&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=flat-square&logo=websocket&logoColor=white)

### Database & Infrastructure
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)
![NGINX](https://img.shields.io/badge/NGINX-009639?style=flat-square&logo=nginx&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=jenkins&logoColor=white)

## 💡 주요 기능

### 1. 전문가 매칭 시스템
- 카테고리별 전문가 검색 및 필터링
- 포트폴리오 기반 전문가 프로필
- 견적 요청 및 매칭 시스템

### 2. 실시간 커뮤니케이션
- WebSocket 기반 실시간 채팅
- 프로젝트 진행 상황 공유
- 파일 첨부 및 공유 기능

### 3. 프로젝트 관리
- 일정 관리 캘린더
- 협업 게시판
- 진행 상황 트래킹

### 4. 결제 및 리뷰
- 안전한 결제 시스템 (포트원 연동)
- 프로젝트 완료 후 리뷰 작성
- 전문가 평점 시스템

## 🏗 시스템 아키텍처
```mermaid
graph TB
subgraph Client
A[Web Browser]
end
subgraph "AWS Cloud"
subgraph "AWS EC2"
B[NGINX Proxy]
C[Spring Boot Application]
D[Jenkins]
end
subgraph "Storage"
E[Image Server]
end
subgraph "Database"
F[MySQL]
end
end
subgraph "External Services"
G[Kakao OAuth]
H[Google OAuth]
I[PortOne Payment]
J[Gmail SMTP]
end
A -->|HTTPS| B
B -->|Reverse Proxy| C
C -->|WebSocket| A
C -->|JDBC| F
C -->|File Upload| E
C <-->|OAuth2| G & H
C <-->|Payment API| I
C -->|Email| J
D -->|CI/CD| C
```

## 🚀 배포 환경

- **서버**: AWS EC2 (Ubuntu 20.04 LTS)
- **웹 서버**: NGINX 1.18
- **컨테이너화**: Docker & Docker Compose
- **CI/CD**: Jenkins Pipeline
- **데이터베이스**: MySQL 8.0
- **모니터링**: Spring Actuator

## 🔒 보안 및 인증

- Spring Security 기반 인증
- OAuth2.0 소셜 로그인 (카카오, 구글)
- HTTPS 보안 통신
- 비밀번호 암호화 (BCrypt)

## 👥 팀 구성 및 역할

**부팀장**: 도건민
- 백엔드 개발 (기여도 75%)
- 프론트엔드 개발 (기여도 50%)
- 서비스 기획 (기여도 85%)
- 인프라 구축 및 배포 (기여도 100%)

## 🖥️ 자료
- [기획 자료](https://dgm0224.tistory.com/1)

## 🔗 링크
- [서비스 링크](https://www.onestack.shop)
