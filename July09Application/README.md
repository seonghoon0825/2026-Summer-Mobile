# July09Application - Android 학습 프로젝트

**작성 날짜**: 2026-07-09

---

## 📱 프로젝트 개요

July09Application은 Android 개발의 핵심 UI 컴포넌트와 레이아웃 기술을 학습하기 위한 멀티 모듈 프로젝트입니다.
각 모듈은 서로 다른 Android UI 패턴과 구현 방식을 다룹니다.

---

## 📚 학습 내용 요약

### 1. **fragmentapp** - Fragment 기반 다중 화면 관리
**학습 목표**: Fragment의 생명주기와 transaction 관리

**주요 기능**:
- `FirstFragment`, `SecondFragment`, `ThirdFragment` 구현
- Fragment 전환 시 버튼 활성화/비활성화 상태 관리
- `FragmentManager.beginTransaction().replace()`를 통한 Fragment 동적 교체
- View Binding을 사용한 UI 레이아웃 바인딩

**학습 포인트**:
- Fragment 컨테이너(`fragmentContainerView`)에 Fragment 동적 로드
- 각 Fragment 전환 시 이전 Fragment의 상태를 새로운 Fragment로 교체
- 버튼 상태 관리로 사용자 경험 최적화

---

### 2. **mydrawer** - Navigation Drawer 구현
**학습 목표**: Material Design NavigationView 패턴 이해

**주요 기능**:
- Material Design 기반 Navigation Drawer 레이아웃 구성
- Edge-to-edge 디스플레이 지원
- AppCompatActivity 확장으로 안정적인 Activity 구현

**학습 포인트**:
- `enableEdgeToEdge()` 활용으로 현대적인 UI 경험 제공
- View Binding을 통한 타입 안전한 레이아웃 관리
- Navigation Drawer를 사용한 앱 메뉴 구조 설계

---

### 3. **recycleview** - RecyclerView 기반 리스트 관리
**학습 목표**: 대규모 데이터 셋 효율적으로 표시하기

**주요 기능**:
- 50개 아이템을 초기에 생성
- `MyAdapter`를 통한 커스텀 어댑터 구현
- 아이템 추가 버튼(`addBtn`) - 새 아이템 동적 추가
- 아이템 삭제 버튼(`delBtn`) - 마지막 아이템 제거
- `LinearLayoutManager`를 통한 선형 레이아웃 관리
- `notifyItemInserted()`, `notifyItemRemoved()`로 어댑터 데이터 변경 반영

**학습 포인트**:
- RecyclerView의 효율적인 뷰 재사용 메커니즘
- 어댑터 패턴을 통한 데이터와 UI 분리
- 동적 리스트 업데이트 시 notification 사용의 중요성
- MutableList를 활용한 데이터 관리

---

### 4. **viewpage** - ViewPager2를 통한 스와이프 네비게이션
**학습 목표**: 수평 스크롤 페이지 네비게이션 구현

**주요 기능**:
- `MyFragmentPagerAdapter` 구현 (FragmentStateAdapter 상속)
- `BlankFragment`, `BlankFragment2`, `BlankFragment3` 페이지 관리
- ViewPager2를 통한 Fragment 페이지 전환
- Swipe 제스처로 페이지 이동

**학습 포인트**:
- FragmentStateAdapter를 통한 Fragment 페이지 관리
- `getItemCount()`로 페이지 개수 정의
- `createFragment()`로 동적 Fragment 생성
- ViewPager2의 modern API와 사용 편의성
- 페이지 간 독립적인 Fragment 생명주기 관리

---

### 5. **app** - 메인 애플리케이션 모듈
**학습 목표**: 멀티 모듈 프로젝트 통합

**주요 기능**:
- 프로젝트의 진입점 역할
- 다른 모듈들을 통합하는 기본 구조 제공

---

## 🎯 핵심 학습 기술 스택

| 기술 | 설명 |
|------|------|
| **Fragment** | 재사용 가능한 UI 컴포넌트, Activity의 일부를 나타냄 |
| **RecyclerView** | 대규모 동적 리스트 효율적으로 표시 |
| **ViewPager2** | 수평 스와이프 네비게이션 구현 |
| **Navigation Drawer** | Material Design 기반 앱 메뉴 |
| **View Binding** | 타입 안전한 뷰 참조 |
| **FragmentStateAdapter** | Fragment 기반 페이져 어댑터 |
| **Kotlin** | 프로젝트의 모든 코드 구현 언어 |

---

## 💡 주요 학습 성과

1. **UI 컴포넌트 이해**: Fragment, RecyclerView, ViewPager2의 동작 원리 숙지
2. **State 관리**: 다중 화면에서 상태 관리 및 동기화 방법 학습
3. **Data Binding**: View Binding을 통한 타입 안전한 UI 개발
4. **어댑터 패턴**: RecyclerView와 ViewPager2에서의 어댑터 구현
5. **Material Design**: 현대적이고 사용자 친화적인 UI 구현

---

## 🚀 프로젝트 구조

```
July09Application/
├── app/                    # 메인 애플리케이션
├── fragmentapp/            # Fragment 학습 모듈
├── mydrawer/              # Navigation Drawer 모듈
├── recycleview/           # RecyclerView 모듈
├── viewpage/              # ViewPager2 모듈
├── build.gradle.kts       # 프로젝트 빌드 설정
└── settings.gradle.kts    # 멀티 모듈 설정
```

---

## 📖 사용 기술

- **Language**: Kotlin
- **Minimum API**: API 21 이상
- **Key Libraries**:
  - AndroidX (AppCompat, RecyclerView, ViewPager2, Fragment)
  - Material Design Components
  - Edge-to-Edge Display Support

---

**마지막 업데이트**: 2026-07-09
