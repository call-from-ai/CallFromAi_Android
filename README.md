# 📞 전화왔어

> AI가 먼저 전화하고 관계를 이어가는, 나만의 연애 시뮬레이션

## 🛠 Tech Stack

<p>
  <strong>Language / Platform</strong><br />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
</p>

<p>
  <strong>Architecture / State</strong><br />
  <img src="https://img.shields.io/badge/Multi_Module-263238?style=for-the-badge&logo=gradle&logoColor=white" />
  <img src="https://img.shields.io/badge/MVVM-1565C0?style=for-the-badge&logoColor=white" />
  <img src="https://img.shields.io/badge/MVI-6D5DF2?style=for-the-badge&logoColor=white" />
  <img src="https://img.shields.io/badge/Orbit_MVI-6D5DF2?style=for-the-badge&logoColor=white" />
  <img src="https://img.shields.io/badge/Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Flow-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
</p>

<p>
  <strong>DI / Navigation</strong><br />
  <img src="https://img.shields.io/badge/Hilt-4285F4?style=for-the-badge&logo=dagger&logoColor=white" />
  <img src="https://img.shields.io/badge/Navigation_3-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
</p>

<p>
  <strong>Network / Serialization</strong><br />
  <img src="https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logoColor=white" />
  <img src="https://img.shields.io/badge/OkHttp-3E4348?style=for-the-badge&logo=square&logoColor=white" />
  <img src="https://img.shields.io/badge/Gson-FF7043?style=for-the-badge&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/OkHttp_SSE-3E4348?style=for-the-badge&logo=square&logoColor=white" />
</p>

<p>
  <strong>Local Data / UI Utility</strong><br />
  <img src="https://img.shields.io/badge/Paging_3-1976D2?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/DataStore-0F9D58?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Room-795548?style=for-the-badge&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/Coil-FF6F00?style=for-the-badge&logoColor=white" />
</p>

<p>
  <strong>Realtime / Push / Logging</strong><br />
  <img src="https://img.shields.io/badge/LiveKit-FF3B5C?style=for-the-badge&logo=webrtc&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase_FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Timber-2E7D32?style=for-the-badge&logoColor=white" />
</p>

<p>
  <strong>Collaboration / Docs</strong><br />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white" />
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white" />
</p>

---

## 📖 프로젝트 소개

AI 캐릭터가 채팅뿐 아니라 **먼저 전화를 걸어오고, 예약 통화를 하고, 관계 상태(연애 온도·스트릭)를 함께 쌓아가는** 연애 시뮬레이션 앱입니다.

**주요 기능**
- 이상형 온보딩 (4단계)
- AI 채팅 (페르소나 기반, 퀵리플라이, SSE 스트리밍)
- AI 음성 통화 (예약 통화 / 즉시 통화)
- 관계 상태 관리 (연애 온도, 스트릭)

- **플랫폼**: Android (Kotlin)

---

## 👤 팀원 소개 및 역할 분담

<div align="center">

| Android | Android | Android | Android |
|:---:|:---:|:---:|:---:|
| <img src="https://github.com/JiwonLee42.png" width="120" /> | <img src="https://github.com/codebidoof.png" width="120" /> | <img src="https://github.com/WAcAW9.png" width="120" /> | <img src="https://github.com/dada4679.png" width="120" /> |
| [JiwonLee42](https://github.com/JiwonLee42)<br />**(LEAD)**<br />통화 · 홈화면 | [codebidoof](https://github.com/codebidoof)<br />채팅 · SSE | [WAcAW9](https://github.com/WAcAW9)<br />FCM · 마이페이지 | [dada4679](https://github.com/dada4679)<br />로그인 · 온보딩 |

</div>
---

## 🛠 기술 스택 상세

| 구분 | 내용 |
|---|---|
| 언어 | Kotlin |
| UI | Jetpack Compose |
| 아키텍처 | Multi Module + MVVM + MVI |
| 비동기 | Coroutines, Flow |
| 상태 관리 | Orbit MVI |
| DI | Hilt |
| 네비게이션 | Navigation 3 |
| 네트워크 | Retrofit, OkHttp, Gson |
| 실시간 채팅 | OkHttp-SSE (Server-Sent Events, 채팅 응답 스트리밍) |
| 실시간 통화 | LiveKit Android SDK (WebRTC 기반) |
| 페이지네이션 | Paging 3 |
| 이미지 로딩 | Coil |
| 푸시 알림 | Firebase Cloud Messaging (FCM) |
| 로컬 저장소 | DataStore, Room |
| 로깅 | Timber |

### 공통 인프라

- 코드 저장소: GitHub
- API 명세: OpenAPI (Swagger) — 백엔드 레포 기준
- 협업 도구: Notion / Figma / Slack

---

## 📂 프로젝트 폴더 구조

```
/
├── app/                         # 앱 진입점(Application), 앱 전역 DI 및 초기화
│
├── build-logic/
│   └── convention/              # Gradle Convention Plugin(공통 빌드 설정)
│
├── core/
│   ├── common/                  # 공통 유틸리티, 확장 함수, 상수, 디스패처, 코루틴 모듈
│   ├── data/                    # Repository 구현체, DTO, Mapper, DataSource
│   ├── database/                # Room Database, DAO, Entity
│   ├── datastore/               # DataStore(사용자 설정, 토큰 등 로컬 데이터)
│   ├── designsystem/            # 공통 UI, Theme, Color, Typography, Component
│   ├── domain/                  # Domain Model, Repository 인터페이스, UseCase
│   └── network/                 # Retrofit, OkHttp, API Service, 네트워크 설정
│
└── feature/
    ├── main/
    │   ├── api/                 # Main 기능의 Navigation 계약(Route)
    │   └── impl/                # Main 화면 및 비즈니스 로직 구현
    │
    ├── login/
    │   ├── api/                 # Login Navigation 계약(Route)
    │   └── impl/                # 로그인 화면 및 인증 기능 구현
    │
    ├── onboarding/
    │   ├── api/                 # Onboarding Navigation 계약(Route)
    │   └── impl/                # 온보딩 화면 및 흐름 구현
    │
    ├── home/
    │   ├── api/                 # Home Navigation 계약(Route)
    │   └── impl/                # 홈 화면 및 기능 구현
    │
    ├── chatting/
    │   ├── api/                 # Chatting Navigation 계약(Route)
    │   └── impl/                # 채팅 화면 및 메시지 기능 구현
    │
    ├── call/
    │   ├── api/                 # Call Navigation 계약(Route)
    │   └── impl/                # 음성/영상 통화 기능 구현
    │
    └── mypage/
        ├── api/                 # MyPage Navigation 계약(Route)
        └── impl/                # 마이페이지 및 사용자 정보 관리 구현
```

---

## 📝 컨벤션 문서

### 브랜치 네이밍 규칙

| 접두사 | 용도 | 예시 |
|---|---|---|
| `feature/` | 신규 기능 개발 | `feature/login` |
| `fix/` | 버그 수정 | `fix/crash-on-home` |
| `refactor/` | 리팩토링 (기능 변화 없음) | `refactor/chat-viewmodel` |
| `chore/` | 빌드, 설정, 의존성 등 | `chore/update-gradle` |
| `docs/` | 문서 작업 | `docs/readme-update` |

- 브랜치명은 소문자와 하이픈(`-`)만 사용
- 이슈 번호가 있는 경우 `feature/123-login` 형태로 이슈 번호 포함 권장

### 커밋 메시지 규칙 (Conventional Commits)

```
<type>: <description>

[선택 사항] 본문
[선택 사항] 이슈 참조 (#123)
```

| type | 설명 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 |
| `style` | 코드 포맷팅, 세미콜론 누락 등 (로직 변경 없음) |
| `refactor` | 리팩토링 |
| `test` | 테스트 코드 추가/수정 |
| `chore` | 빌드 설정, 패키지 매니저 설정 등 |

예시:
```
feat: 로그인 화면 구현
fix: 홈 화면 진입 시 크래시 수정 (#42)
refactor: 채팅 뷰모델 상태관리 구조 개선
```

### PR 규칙

- **제목**: 커밋 메시지 규칙과 동일한 형식 사용 (예: `feat: 통화 수신 UI 구현`)
- **템플릿**: 변경 사항, 스크린샷(UI 변경 시 필수), 테스트 방법, 관련 이슈 링크 포함
- **리뷰어**: 팀원 1명 지정 (최소 1명 승인 필요)
- **머지 조건**: 리뷰 승인 1개 이상 + CI 통과 시에만 머지 가능
- **머지 방식**: Squash and Merge (커밋 히스토리 정리)
- 본인 PR은 본인이 머지하지 않음

### 코드 네이밍 규칙

| 대상 | 규칙 | 예시 |
|---|---|---|
| 클래스, 인터페이스 | PascalCase | `ChatRoomViewModel` |
| 함수, 변수 | camelCase | `fetchChatHistory()` |
| 상수 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Composable 함수 | PascalCase (명사형) | `ChatBubble()` |
| 리소스 파일(Android) | snake_case | `ic_call_incoming.xml` |
| 패키지명 | 전부 소문자 | `com.callmeai.chat` |

- Boolean 변수/함수는 `is`, `has`, `should` 접두사 사용 (`isLoading`, `hasPermission`)
- 약어는 대문자 통일 지양, 카멜케이스 유지 (팀 내 IDE 포맷터 공유)

### 패키지 구조 규칙

- feature 모듈은 기능 단위로 분리하고, 외부에 노출할 Navigation 계약은 `api`, 화면/상태/비즈니스 로직 구현은 `impl`에 둠
- 공통 UI는 `core:designsystem`, 공통 유틸은 `core:common`에 두되, 특정 feature에서만 쓰는 코드는 공통 모듈로 올리지 않음
- 데이터 흐름은 단방향으로 유지하고, 의존성은 `feature → core:domain ← core:data` 구조를 기준으로 순환 참조를 방지함. 단, `core:datastore` 모듈은 각 feature 모듈의 의존할 수 있도록 허용
- 화면 상태는 ViewModel에서 Orbit MVI Container로 관리하고, UI는 State와 SideEffect를 구독해 렌더링함
