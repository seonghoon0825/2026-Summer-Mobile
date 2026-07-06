# JulySixApplication

간단하고 직관적인 스톱워치 기능을 제공하는 Android 애플리케이션입니다.

## 📱 프로젝트 개요

JulySixApplication은 시간을 측정하고 관리하기 위한 기본적인 스톱워치 애플리케이션입니다. 시작, 중지, 초기화 기능을 통해 사용자가 쉽게 시간을 측정할 수 있습니다.

## 🛠️ 기술 스택

- **언어**: Kotlin
- **최소 API 레벨**: 29
- **타겟 API 레벨**: 37
- **빌드 도구**: Gradle
- **UI 프레임워크**: Android Jetpack

### 주요 라이브러리

- `androidx.appcompat` - AndroidX 호환성 지원
- `androidx.constraintlayout` - 반응형 레이아웃
- `androidx.core.ktx` - Kotlin 확장 기능
- `material` - Material Design 컴포넌트
- `androidx.activity.ktx` - Activity 확장 기능

## 📋 프로젝트 구조

```
JulySixApplication/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/kr/hnu/ice/julysixapplication/
│   │   │   │   └── MainActivity.kt          # 메인 액티비티 (스톱워치 로직)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml    # UI 레이아웃
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml          # 문자열 리소스
│   │   │   │       ├── colors.xml           # 색상 정의
│   │   │   │       └── themes.xml           # 테마 설정
│   │   │   └── AndroidManifest.xml          # 앱 설정 파일
│   │   ├── test/                            # 단위 테스트
│   │   └── androidTest/                     # Android 테스트
│   └── build.gradle.kts                     # 앱 레벨 빌드 설정
├── build.gradle.kts                         # 프로젝트 레벨 빌드 설정
└── settings.gradle.kts                      # Gradle 설정

```

## ✨ 주요 기능

### 1. **시간 측정 (Start)**
- 버튼을 누르면 시간 측정 시작
- Chronometer 위젯으로 실시간 시간 표시
- 이전 측정 시간에서 계속 측정 가능

### 2. **시간 일시중지 (Stop)**
- 버튼을 누르면 시간 측정 중지
- 측정 중인 시간 보존
- 나중에 다시 시작할 수 있음

### 3. **시간 초기화 (Reset)**
- 측정 시간을 0으로 초기화
- 새로운 측정을 시작할 준비

### 4. **백버튼 보호**
- 앱 시작 후 5초 이내에 백버튼 누르면 경고 메시지 표시
- 5초 이상 경과 후에만 앱 종료 가능
- 의도하지 않은 앱 종료 방지

## 🎨 UI 구성

### 레이아웃 요소

| 요소 | 설명 |
|------|------|
| **헤더** | "측정 시간" 텍스트 |
| **Chronometer** | 측정 중인 시간 표시 (빨간색, 48sp) |
| **시작 버튼** | 시간 측정 시작 |
| **중지 버튼** | 시간 측정 일시중지 |
| **초기화 버튼** | 측정 시간 초기화 |

## 🔧 주요 구현 로직

### 시간 측정 로직
```kotlin
// 시작 버튼
binding.chronometer.setBase(SystemClock.elapsedRealtime() + elapsedTime)
binding.chronometer.start()

// 중지 버튼
elapsedTime = binding.chronometer.base - SystemClock.elapsedRealtime()
binding.chronometer.stop()

// 초기화 버튼
elapsedTime = 0L
binding.chronometer.base = SystemClock.elapsedRealtime()
```

### 버튼 상태 관리
- **시작 중**: 시작 버튼 비활성화, 중지 버튼 활성화
- **정지 중**: 시작 버튼 활성화, 중지 버튼 비활성화

### 사용자 피드백
- Toast 메시지로 시작/중지 상태 알림
- 백버튼 5초 보호 기능으로 사용자 안내

## 📱 화면 지원

- **기본 레이아웃**: `activity_main.xml`
- **가로 모드**: `layout-land/activity_main.xml`

## 🚀 빌드 및 실행

### 빌드
```bash
./gradlew build
```

### APK 생성
```bash
./gradlew assembleRelease
```

### Android Studio에서 실행
1. Android Studio에서 프로젝트 열기
2. "Run" → "Run 'app'" 선택
3. 대상 디바이스 선택 후 실행

## 📝 주요 설정

- **View Binding**: 활성화 (type-safe 뷰 참조)
- **Java 호환성**: Java 11
- **Soft Input Mode**: adjustResize (키보드 입력 시 레이아웃 조정)

## 🔒 보안 & 백업

- 앱 백업 활성화
- 데이터 추출 규칙 적용

## 📌 버전 정보

- **앱 버전**: 1.0
- **버전 코드**: 1

## 💡 향후 개선 사항

- 랩 타임 기능 추가
- 측정 시간 저장 및 히스토리 관리
- 사운드 알림 기능
- 애니메이션 효과 개선
- 다크 모드 지원 강화

## 👨‍💻 개발자

- 개발 기관: HNU ICE (한남대학교 ICE)
- 패키지명: `kr.hnu.ice.julysixapplication`

## 📄 라이선스

이 프로젝트는 ICE 프로그램의 일부입니다.

---

**마지막 업데이트**: 2026년 7월 6일
