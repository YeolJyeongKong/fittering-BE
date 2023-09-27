# 핏터링(fittering) Back-end
**핏터링**은 체형 분석을 쉽게 하고, 나의 체형에 fit한 옷을 추천해주는 쇼핑몰 서비스입니다.<br>
[fittering-BE](https://github.com/YeolJyeongKong/fittering-BE)는 핏터링 서비스의 **백엔드 코드를 관리**하는 레포지토리입니다.<br>
<br>

## 📆 개발 기간
2023.06 ~ 2023.11<br>
<br>

## 프로젝트 구조
다음은 [fittering-BE](https://github.com/YeolJyeongKong/fittering-BE) 구조입니다.<br>
MVC 패턴에 맞게 디렉토리를 분리했으며, 각 레벨에서 DTO를 관리하도록 구성했습니다.
```
src
├─ main
│  ├─ java
│  │  └─ fittering
│  │     └─ mall
│  │        ├─ config
│  │        │  ├─ auth
│  │        │  │  └─ domain
│  │        │  ├─ cache
│  │        │  ├─ jwt
│  │        │  └─ kafka
│  │        │     └─ domain
│  │        │        └─ dto
│  │        ├─ controller
│  │        │  └─ dto
│  │        │     ├─ request
│  │        │     └─ response
│  │        ├─ domain
│  │        │  ├─ collection
│  │        │  ├─ entity
│  │        │  └─ mapper
│  │        ├─ repository
│  │        │  ├─ dto
│  │        │  └─ querydsl
│  │        ├─ scheduler
│  │        │  └─ dto
│  │        └─ service
│  │           └─ dto
│  └─ resources
└─ test
```
- `collection` : 일급 컬렉션 관리
- `mapper` : **MapStruct** Mapper 인터페이스 관리
- `test` : Service 내 각 함수에 대한 **JUnit** 테스트 코드 관리
<br>

## 아키텍처
### 전체
핏터링 서비스는 다음과 같은 아키텍처로 구성되어 있습니다.
<img width="1185" alt="전체아키텍쳐" src="https://github.com/YeolJyeongKong/fittering-BE/assets/61930524/bf717a0e-dd98-41b9-a36a-04a56a2dcea1">
<br>

### 백엔드
핏터링 서비스의 백엔드는 다음과 같은 아키텍처로 구성되어 있습니다.
<p align="center">
  <img width="550" alt="백엔드아키텍쳐" src="https://github.com/YeolJyeongKong/fittering-BE/assets/61930524/8cce9495-2f9c-49a0-aaba-1862e44de452">
</p>
<br>

다음은 각 기술을 **사용한 이유**와 블로그에 기록한 📄 **기술 적용기**, ➰ **관련 PR**입니다.
- **Spring Data JPA, Querydsl**
  + **개발 생산성 향상**을 위해 적용
  + 📄
    - [[Spring] 스프링 데이터 JPA 설정 및 쿼리 메소드](https://yooniversal.github.io/study/post209/)
    - [[Spring] Querydsl를 활용한 Repository와 Controller 개발](https://yooniversal.github.io/study/post212/)
- **Redis**
  + **API 응답 속도 향상을 위한** 캐싱 적용 및 `Spring scheduler`와 함께 배치 업데이트에 활용
  + 📄
    - [[Spring] Redis로 캐시 적용하기](https://yooniversal.github.io/project/post222/)
    - [[Spring] Redis 캐시를 활용해 데이터 주기적으로 반영하기](https://yooniversal.github.io/project/post251/)
  + ➰  [Redis 캐시 : 조회수 배치 업데이트 설정 #39](https://github.com/YeolJyeongKong/fittering-BE/pull/39)
- **JWT + OAuth2**
  + **사용자 인증/인가 처리** 수단으로 `JWT` 활용 및 **소셜 로그인** 기능 지원
  + 📄
    - [[Spring] JWT로 인증 구현하기](https://yooniversal.github.io/project/post219/)
    - [[Spring] 카카오, 구글 OAuth + JWT](https://yooniversal.github.io/project/post255/)
    - [[Spring] JWT 예외 처리 필터 설정하기](https://yooniversal.github.io/project/post256/)
  + ➰
    - [JWT 적용 및 일부 코드 수정 #2](https://github.com/YeolJyeongKong/fittering-BE/pull/2)
    - [OAuth : JWT 적용 및 Apple 로그인 구현 등 #48](https://github.com/YeolJyeongKong/fittering-BE/pull/48)
- **Grafana + Prometheus**
  + 서버 자원 정보 **수집 및 모니터링**
  + 📄  [[Spring] Grafana + Prometheus 연동해서 모니터링 하기](https://yooniversal.github.io/project/post226/)
  + ➰  [Grafana + Prometheus 적용 및 추천 기능 수정 #4](https://github.com/YeolJyeongKong/fittering-BE/pull/4)
- **ELK** (Logstash + Elasticsearch + Kibana)
  + 서버에서 발생하는 로그 **수집 및 모니터링**
  + 📄  [[Spring] 스프링 부트에 ELK 적용하기](https://yooniversal.github.io/project/post257/)
  + ➰  [ELK 설정 : 로그 분석 및 시각화 #78](https://github.com/YeolJyeongKong/fittering-BE/pull/78)
- **Kafka**
  + 운영 DB와 크롤링 DB 간 **데이터 동기화** 수행
  + 📄
    - [[Spring] Kafka 적용하기](https://yooniversal.github.io/project/post259/)
    - [[Spring] Kafka 세부 설정하기](https://yooniversal.github.io/project/post265/)
  + ➰
    - [Kafka 적용 : DB 간 데이터 동기화 #80](https://github.com/YeolJyeongKong/fittering-BE/pull/80)
    - [버그 수정 : DB 간 동기화 시 모든 상품 데이터를 반영하지 못하는 문제 #120](https://github.com/YeolJyeongKong/fittering-BE/pull/120)
- **Docker**
  + `Redis`, `Grafana`, `Prometheus`, `ELK`를 **컨테이너 환경**에서 관리
- **GitHub Actions**
  + 프로젝트 자동화 배포를 위해 **CI/CD** 적용
  + 📄  [[Spring] Github Actions로 EC2에 자동화 배포 적용](https://yooniversal.github.io/project/post240/)
  + ➰
    - [TDD, CI/CD #8](https://github.com/YeolJyeongKong/fittering-BE/pull/8)
    - [CD : docker compose 등록 #14](https://github.com/YeolJyeongKong/fittering-BE/pull/14)
    - [Github Actions : 무중단 배포 설정 완료 #17](https://github.com/YeolJyeongKong/fittering-BE/pull/17)
<br>

### Swagger
팀 내 API 문서 공유를 위해 **Swagger**를 적용했습니다.<br>
아래는 문서 내용 일부로, 전체 문서는 [여기](https://sub.fit-tering.com/swagger-ui/index.html)에서 확인하실 수 있습니다.<br>

<img width="730" alt="image" src="https://github.com/YeolJyeongKong/fittering-BE/assets/61930524/b919cff5-9d6c-469a-8024-0e9143935e6c">
<br>

- URI에 `/auth`이 포함된 API는 사용 시 권한이 필요함을 의미
- 📄  [[Swagger] API 문서 만들기](https://yooniversal.github.io/project/post218/)
- ➰  [테이블 등록, 로그인/회원가입 구현, API 문서화 #1](https://github.com/YeolJyeongKong/fittering-BE/pull/1)
<br>

## ERD
![fittering-backend-ERD](https://github.com/YeolJyeongKong/fittering-BE/assets/61930524/cbaf6bb5-b89c-473a-bb3d-4d2c5a6ae55c)
- 생성 날짜 `created_at`, 최종 수정 날짜 `updated_at`은 모든 엔티티의 공통 필드
<br>

## 💻 개발환경
```
- IntelliJ IDEA Ultimate 2023.1.3
- Java 17
- Spring Boot 3.1.1
- Spring Security 6.1.1
- Gradle 7.6.1
- MySQL 8.0.33
- ELK 7.6.2
```
<br>

## 📑 트러블 슈팅
- [[Spring Security] There is no PasswordEncoder mapped for the id “null”](https://yooniversal.github.io/project/post214/)
- [[Spring] ClassNotFoundException: javax.xml.bind.DatatypeConverter](https://yooniversal.github.io/project/post220/)
- [[Spring] cannot deserialize from Object value](https://yooniversal.github.io/project/post223/)
- [[Prometheus] Get “http://host.docker.internal:8080/login”: stopped after 10 redirects](https://yooniversal.github.io/project/post224/)
- [[OAuth2] This class supports client_secret_basic, client_secret_post, and none by default](https://yooniversal.github.io/project/post227/)
- [[Spring] TransientPropertyValueException: object references an unsaved transient instance](https://yooniversal.github.io/project/post231/)
- [[Spring] Cannot construct instance of org.springframework.data.domain.PageImpl](https://yooniversal.github.io/project/post234/)
- [[Spring] java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree](https://yooniversal.github.io/project/post239/)
- [[Spring] java.lang.UnsupportedOperationException](https://yooniversal.github.io/project/post244/)
- [[Spring] actual and formal argument lists differ in length](https://yooniversal.github.io/project/post245/)
- [[Spring] JWT를 적용하면서 발생한 에러 정리](https://yooniversal.github.io/project/post249/)
- [[Spring] IllegalStateException: Ambiguous handler methods](https://yooniversal.github.io/project/post264/)
- [[Spring] multipart.MultipartException: Current request is not a multipart request](https://yooniversal.github.io/project/post263/)
- [more details...](https://yooniversal.github.io/Project)
