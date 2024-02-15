### relpl 프로젝트

---

#### 개요

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

#### 설계

- 사용한 기술 스택

- 시스템 아키텍쳐

- ERD 다이어그램

- 요구사항 명세서

- API 명세서

---

#### 사용한 기술 (상세)

- 사용한 라이브러리

- 디렉토리 구조

- 패키지 다이어그램

- 인증 처리과정

---

##### Role & Responsibility (R&R)

###### 김성훈

- 인프라 (CI/CD)

- 릴레이 플로깅 생성 기능

- Tmap API 를 활용한 초기 도로 DB 구축

- 경로 추천 기능

###### 최재성

- jwt , Spring Security 를 이용한 회원가입 API 개발

- jwt , Spring Security 를 이용한 로그인 API 개발

- 릴레이 플로깅 참여 API 개발

- 릴레이 플로깅 중단 API 개발

###### 김효주

- Redis 를 활용한 실시칸 랭킹 API 개발

- PostGreSQL 활용하여 Amazon S3 사진 업로드 가능한 프로필 설정 API 개발

- PostGreSQL 활용하여 프로필 사진 변경이 가능한 마이페이지 API 개발

- mongoDB, PostGreSQL, Spring Data 를 활용한 내 플로깅 기록 보기 API 개발

###### 송민석

- 포인트 관련 기능

- 플로깅 장소 제보 기능

- 릴레이 플로깅 정보 조회 기능
