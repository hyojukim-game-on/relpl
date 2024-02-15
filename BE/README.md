 <h1 align="center">
릴플🍀
</h1>

<div align="center">
<img src="https://blog.kakaocdn.net/dn/o6NbZ/btsEP7zFkus/EvoxawE97IKX8CEcse8zJ0/tfile.svg" width="400px">
</div>

## 개요

- 한 줄 요약 : *relpl* 프로젝트는 **위치 기반 릴레이 플로깅이 가능**한 **Android 모바일 어플리케이션**입니다.

- 기획의도 : 효율적인 플로깅이 가능하도록 돕고, 지속적인 플로깅을 위한 동기 부여가 가능하도록 하고자 했습니다.

- 개발 인원 및 기간
  
  - 개발 인원 : Android 2명, BackEnd 4명
  
  - 개발 기간 : 2024.01.03 ~ 2024.02.16 (총 45일, Business Day 31일)

- 주요 기능
  
  - 유저 관련 기능
  
  - 릴레이 플로깅 관련 기능
  
  - 제보, 보상 관련 기능

- 데모 시연 영상 : [D201 UCC (youtube.com)](https://www.youtube.com/watch?v=hv8uWsDKsy0)

---
<br/><br/><br/>

## 설계

### 시스템 아키텍쳐
![](https://blog.kakaocdn.net/dn/SIWQC/btsEVOToYqw/9lDCjOphFtE0zlKzCFSWBK/img.png)

### ERD 다이어그램
![](https://blog.kakaocdn.net/dn/IV5N0/btsERlFbYuc/Du9wAf3ra1WO7tSL3uvfb0/img.png)

- [요구사항 명세서](https://docs.google.com/spreadsheets/d/1ZayfMIxvD49W1cAYiXdvZzePvYK2iXs2G8KfIWohgio/edit?usp=sharing)

- [API 명세서](https://grand-check-7ba.notion.site/API-33f7e2f1f2e14a1683b82493a37beebd?pvs=4)

---

## 사용한 기술

### 사용한 라이브러리

- Spring Boot Data JPA, MongoDB, Redis
- Project Lombok
- PostgreSQL JDBC Driver
- Spring Boot Starter Test
- Spring Security Test
- SpringDoc OpenAPI
- Hibernate Spatial
- Jakarta Annotations & Persistence API
- Spring Cloud AWS
- Jasypt
- Spring Boot Starter Security
- JSON Web Token (JWT)
- Spring Boot Starter Cache
- Firebase Admin SDK
- OkHttp

### 디렉토리 구조
![](https://blog.kakaocdn.net/dn/4igCf/btsERklWOxL/quhS0fCz2XSxIo3ZIkJ9J1/img.png)

![](https://blog.kakaocdn.net/dn/tsVZV/btsES6AMKdH/gdLQvDmJSmXtwRswgXhDe1/img.png)


### 인증 처리과정


---

<br/><br/><br/><br/><br/>
## Back-End Role & Responsibility (R&R)

#### 김성훈

- 인프라 (CI/CD)

- 릴레이 플로깅 생성 기능

- Tmap API 를 활용한 초기 도로 DB 구축

- 경로 추천 기능

#### 최재성

- jwt , Spring Security 를 이용한 회원가입 API 개발

- jwt , Spring Security 를 이용한 로그인 API 개발

- 릴레이 플로깅 참여 API 개발

- 릴레이 플로깅 중단 API 개발

#### 김효주

- Redis 를 활용한 실시칸 랭킹 API 개발

- PostGreSQL 활용하여 Amazon S3 사진 업로드 가능한 프로필 설정 API 개발

- PostGreSQL 활용하여 프로필 사진 변경이 가능한 마이페이지 API 개발

- mongoDB, PostGreSQL, Spring Data 를 활용한 내 플로깅 기록 보기 API 개발

#### 송민석

- 포인트 관련 기능

- 플로깅 장소 제보 기능

- 릴레이 플로깅 정보 조회 기능


