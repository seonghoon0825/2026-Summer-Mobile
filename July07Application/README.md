# July07Application

**학습 날짜:** 2026-07-07 (월)

## 📋 프로젝트 개요

Android에서 Activity 간 화면 전환을 구현하고, 각 화면에서 다른 화면으로 자유롭게 이동할 수 있는 예제 애플리케이션입니다.

## 🎯 학습 목표

- Activity 간의 Intent를 이용한 화면 전환 구현
- ConstraintLayout을 이용한 레이아웃 설계
- ViewBinding을 이용한 UI 요소 제어
- 각 화면에서의 버튼 클릭 이벤트 처리

## 🏗️ 앱 구조

### Main Activity (메인 화면)
- **배경색:** 기본 (White)
- **기능:**
  - "두번째 화면" 버튼 → Second Activity로 이동
  - "세번째 화면" 버튼 → Third Activity로 이동
  - "네번째 화면" 버튼 → Fourth Activity로 이동

### Second Activity (두 번째 화면)
- **배경색:** 노란색 (#CDDC39)
- **기능:**
  - "메인 화면으로" 버튼 → Main Activity로 이동
  - "세 번째 화면으로" 버튼 → Third Activity로 이동
  - "네 번째 화면으로" 버튼 → Fourth Activity로 이동
  - "종료" 버튼 (빨강) → Activity 종료 + Toast 메시지 표시

### Third Activity (세 번째 화면)
- **배경색:** 빨간색 (#FF0F0F)
- **기능:**
  - "메인 화면으로" 버튼 → Main Activity로 이동
  - "두 번째 화면으로" 버튼 → Second Activity로 이동
  - "네 번째 화면으로" 버튼 → Fourth Activity로 이동
  - "종료" 버튼 (빨강) → Activity 종료 + Toast 메시지 표시

### Fourth Activity (네 번째 화면)
- **배경색:** 남색 (#3F51B5)
- **기능:**
  - "메인 화면으로" 버튼 → Main Activity로 이동
  - "두 번째 화면으로" 버튼 → Second Activity로 이동
  - "세 번째 화면으로" 버튼 → Third Activity로 이동
  - "종료" 버튼 (빨강) → Activity 종료 + Toast 메시지 표시

## 📁 파일 구조

```
July07Application/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/kr/hnu/ice/july07application/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── SecondActivity.kt
│   │   │   │   ├── ThirdActivity.kt
│   │   │   │   └── FourthActivity.kt
│   │   │   ├── res/
│   │   │   │   └── layout/
│   │   │   │       ├── activity_main.xml
│   │   │   │       ├── activity_second.xml
│   │   │   │       ├── activity_third.xml
│   │   │   │       └── activity_fourth.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle.kts
└── README.md
```

## 🔑 핵심 기술

### 1. Intent를 이용한 화면 전환
```kotlin
val intent = Intent(this, SecondActivity::class.java)
startActivity(intent)
```

### 2. ViewBinding을 이용한 UI 제어
```kotlin
val binding = ActivitySecondBinding.inflate(layoutInflater)
setContentView(binding.root)

binding.finishBtn.setOnClickListener {
    finish()
}
```

### 3. Toast 메시지
```kotlin
Toast.makeText(this, "메시지", Toast.LENGTH_SHORT).show()
```

### 4. ConstraintLayout 제약 조건
- `app:layout_constraintStart_toStartOf="parent"` - 좌측 정렬
- `app:layout_constraintEnd_toEndOf="parent"` - 우측 정렬
- `app:layout_constraintTop_toTopOf="parent"` - 상단 정렬
- `app:layout_constraintBottom_toBottomOf="parent"` - 하단 정렬

## 💡 학습 내용 요약

### Activity 생명주기 관리
- `onCreate()`: Activity 초기화 및 UI 설정
- `finish()`: Activity 종료

### Intent 사용법
- 같은 앱 내에서 Activity 전환
- 암시적 Intent vs 명시적 Intent (이 프로젝트는 명시적 Intent 사용)

### UI 레이아웃 설계
- ConstraintLayout을 이용한 반응형 UI
- LinearLayout을 이용한 수직/수평 배치
- 버튼 배치 및 색상 지정

### ViewBinding의 장점
- null 안전성 제공
- Type 안전성 제공
- findViewById()보다 성능 우수

## 🚀 실행 방법

1. Android Studio에서 프로젝트 열기
2. 에뮬레이터 또는 실제 기기 연결
3. `Run 'app'` 실행
4. 메인 화면에서 원하는 화면으로 이동
5. 각 화면의 버튼을 클릭하여 다른 Activity로 전환

## ✅ 완성된 기능

- [x] 4개의 Activity 구현
- [x] 각 Activity 간 상호 이동 가능
- [x] Second, Third, Fourth Activity에 종료 버튼 추가
- [x] 종료 시 Toast 메시지 표시
- [x] 배경색으로 화면 구분
- [x] ConstraintLayout 제약 조건 최적화
- [x] 빌드 성공 (Lint 에러 해결)

## 📌 주요 수정 사항

### 2026-07-07 수정 내용
- **activity_second.xml**: 네비게이션 버튼 4개 추가 (Main, Third, Fourth, 종료)
- **activity_third.xml**: 네비게이션 버튼 4개 추가 (Main, Second, Fourth, 종료)
- **activity_fourth.xml**: 네비게이션 버튼 4개 추가 (Main, Second, Third, 종료)
- **SecondActivity.kt**: Intent를 통한 화면 전환 로직 구현
- **ThirdActivity.kt**: Intent를 통한 화면 전환 로직 구현
- **FourthActivity.kt**: Intent를 통한 화면 전환 로직 구현
- **activity_main.xml**: ConstraintLayout 제약 조건 수정 (Lint 에러 해결)

## 🎓 배운 개념

| 개념 | 설명 |
|------|------|
| **Intent** | 앱 내 또는 앱 간의 작업을 정의하고 실행하는 메커니즘 |
| **Activity** | 사용자와 상호작용하는 앱의 진입점 |
| **ViewBinding** | 리소스 ID 대신 객체로 뷰에 접근 |
| **Toast** | 화면에 잠시 나타나는 간단한 메시지 |
| **ConstraintLayout** | 유연한 위치 지정 방식의 레이아웃 |
| **finish()** | 현재 Activity 종료 및 이전 Activity로 돌아가기 |

## 🔗 관련 Android 공식 문서

- [Activity 생명주기](https://developer.android.com/guide/components/activities/intro-activities)
- [Intent 및 Intent 필터](https://developer.android.com/guide/components/intents-filters)
- [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
- [ConstraintLayout](https://developer.android.com/training/constraint-layout)

---

**프로젝트 완성 날짜:** 2026-07-07  
**상태:** ✅ 완료  
**빌드 결과:** BUILD SUCCESSFUL
