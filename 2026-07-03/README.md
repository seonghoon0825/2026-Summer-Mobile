# 모바일프로그래밍 기말프로젝트 기획 보고서
## 무신사(MUSINSA) 벤치마킹 기반 멀티모듈 커머스 앱

### 학번 : 20210514 
### 이름 : 정성훈
---

## 목차
1. [프로젝트 개요](#1-프로젝트-개요)
2. [벤치마킹 앱 선정](#2-벤치마킹-앱-선정)
3. [기술 스택](#3-기술-스택)
4. [전체 아키텍처 및 모듈 구조](#4-전체-아키텍처-및-모듈-구조)
5. [모듈별 상세 명세 (13개)](#5-모듈별-상세-명세-13개)
6. [벤치마킹 대비 개선·추가 기능](#6-벤치마킹-대비-개선추가-기능)
7. [모듈별 다뤄야 할 핵심 기술](#7-모듈별-다뤄야-할-핵심-기술)

---

## 1. 프로젝트 개요

### 1.1 프로젝트 소개
저는 이번 모바일프로그래밍 기말프로젝트로 **패션 커머스(쇼핑) 앱**을 만들려고 합니다.

평소에 온라인 쇼핑을 자주 하는데, 여러 앱을 둘러보다가 **무신사(MUSINSA)** 앱을 알게 되었습니다. 무신사는 옷을 카테고리별로 깔끔하게 보여주고, 상품 상세에서 색상·사이즈 옵션을 고르고, 실제 착용한 사진 리뷰까지 볼 수 있다는 점이 마음에 들었습니다. 화면 구성이 직관적이면서도, 홈 → 카테고리 → 검색 → 상품 상세 → 장바구니 → 주문으로 이어지는 쇼핑 흐름이 명확하게 짜여 있어서 벤치마킹 대상으로 딱 좋다고 생각했습니다.

그래서 저는 **무신사를 벤치마킹해서, 핵심 쇼핑 기능을 직접 구현한 커머스 앱을 만들려고 합니다.** 단순히 화면만 따라 만드는 것이 아니라, 앱을 기능·계층별로 나눈 **멀티모듈 구조(10개 이상의 모듈)**로 설계해서 실무에서 쓰는 아키텍처를 경험하는 것을 목표로 합니다.

또한 원본 앱을 그대로 복제하는 데 그치지 않고, 제 나름대로 **기능을 개선·추가**하려고 합니다. 찜한 상품이 세일하면 알림을 주는 기능, 최근 본 상품을 기반으로 추천을 해주는 기능, 장바구니에서 쿠폰·적립까지 실시간으로 계산해주는 기능, 주문 상태를 타임라인으로 보여주는 UI 등을 넣어서 원본보다 더 편리하게 만들고 싶습니다. 이렇게 해서 "무신사를 벤치마킹했지만, 여기에 내가 만든 개선점이 더해진 앱"을 완성하는 것이 이번 프로젝트의 목표입니다.

### 1.2 프로젝트 목표
기존 상용 앱(무신사)을 벤치마킹하여 핵심 기능을 재구현하고, 여기에 독자적인 기능을 수정·추가한다. 특히 앱을 **기능·계층별로 분리한 멀티모듈(Multi-Module) 아키텍처**로 구성하여, 10개 이상의 Gradle 모듈로 구현하는 것을 목표로 한다.

### 1.3 과제 요구사항 대응
| 요구사항 | 대응 방안 |
|---|---|
| ① 기존 앱 벤치마킹 후 기능 수정·추가 | 무신사의 커머스 흐름을 재구현하고, 6가지 개선·추가 기능 도입(6장) |
| ② New Module 10개 이상 구현 | Gradle 멀티모듈로 총 13개 모듈 설계(5장) |

---

## 2. 벤치마킹 앱 선정

### 2.1 선정 앱: 무신사 (MUSINSA)
국내 대표 패션 커머스 플랫폼. 오픈마켓형 쇼핑 앱 중에서 학습·발표 프로젝트에 가장 적합하다고 판단하여 선정했다.

### 2.2 선정 이유
- **시각적 완성도**: 패션 이미지 중심 UI라 시연·발표 시 화면이 화려하고 어필이 잘 된다.
- **명확한 기능 경계**: 홈·카테고리·검색·상품상세·장바구니·주문·리뷰 등 기능이 뚜렷이 나뉘어 있어 모듈 분리가 자연스럽다.
- **적절한 기술 난이도**: 결제·물류 같은 과도한 복잡도(예: 쿠팡 로켓배송) 없이, 커머스 핵심 흐름을 완결성 있게 구현할 수 있다.
- **차별화 여지**: 사이즈·색상 옵션 선택, 포토리뷰(스냅) 등 개선·확장할 지점이 많다.

### 2.3 벤치마킹 핵심 기능 분석
무신사의 주요 사용자 흐름을 분석하여 다음 기능군을 도출했다.

| 기능군 | 원본 앱의 기능 |
|---|---|
| 홈/추천 | 기획전, 랭킹, 추천 상품 피드 |
| 카테고리 | 대/중/소 분류 탐색 |
| 검색 | 키워드 검색, 필터(브랜드·가격·사이즈), 정렬 |
| 상품 상세 | 이미지 갤러리, 옵션(색상/사이즈) 선택, 리뷰 요약 |
| 장바구니 | 담기, 수량·옵션 변경, 선택 주문 |
| 주문/결제 | 주문서 작성, 배송지, 결제 |
| 리뷰/스냅 | 포토리뷰, 별점, 착용 스냅 |
| 마이페이지 | 주문내역, 찜, 내 리뷰, 설정 |
| 로그인 | 회원가입·로그인 |

---

## 3. 기술 스택

| 구분 | 사용 기술 | 비고 |
|---|---|---|
| 언어 | Kotlin | |
| UI | Jetpack Compose | 선언형 UI, 멀티모듈 궁합 우수 |
| 아키텍처 | MVVM + Multi-Module | 단방향 데이터 흐름(UDF) |
| 의존성 주입 | Hilt | 모듈 간 DI에 사실상 필수 |
| 비동기 | Coroutines + Flow | |
| 네트워크 | Retrofit + OkHttp | 또는 Firebase |
| 로컬 DB | Room | 장바구니·찜·최근 본 상품 |
| 경량 저장소 | DataStore | 로그인 토큰·설정 |
| 이미지 로딩 | Coil | |
| 화면 이동 | Navigation Compose | 모듈 간 이동 |
| 백엔드(택1) | Firebase(Auth·Firestore·Storage) **또는** Mock JSON | 아래 3.1 참고 |

### 3.1 백엔드 방식 선택
- **Firebase 방식**: 실제 인증·상품 데이터 저장·이미지 업로드 가능. 서버 구축 없이 완결성 높은 앱 구현.
- **Mock JSON 방식**: 서버 부담 없이 로컬 JSON으로 상품 데이터 구성. 빠른 개발.

---

## 4. 전체 아키텍처 및 모듈 구조

### 4.1 모듈 구성 (총 13개)
```
:app                     (앱 진입점)
│
├── core (인프라 계층, 4개)
│   ├── :core:model          데이터 모델
│   ├── :core:designsystem   공용 UI·테마
│   ├── :core:data           Repository·네트워크·로컬 통합
│   └── :core:common         유틸·확장함수
│
└── feature (기능 계층, 8개)
    ├── :feature:auth          로그인/회원가입
    ├── :feature:home          홈·추천
    ├── :feature:category      카테고리
    ├── :feature:search        검색
    ├── :feature:productdetail 상품 상세
    ├── :feature:cart          장바구니
    ├── :feature:order         주문/결제
    └── :feature:mypage        마이페이지(찜·주문내역·리뷰)
```

### 4.2 의존성 방향 (단방향, 순환 참조 없음)
```
app  ─────────────►  feature:*  ─────────────►  core:data  ──►  core:model
                         │                          │
                         └──►  core:designsystem     └──►  core:common
                         └──►  core:common
```
- `feature` 모듈끼리는 **서로 직접 의존하지 않는다.** 화면 전환은 `:app` 레벨의 Navigation Graph가 담당한다.
- 모든 화살표는 한 방향이며 **순환 참조가 없다.** (발표 시 핵심 어필 포인트)
- 하위 계층(core)일수록 프레임워크 의존성이 적고 재사용성이 높다.

### 4.3 멀티모듈을 채택한 이유 (평가 어필)
- **관심사 분리**: 각 기능이 독립 모듈로 격리되어 유지보수·협업이 쉽다.
- **빌드 속도 개선**: 변경된 모듈만 재컴파일 → Gradle 병렬·증분 빌드 이점.
- **재사용성**: `core` 모듈은 여러 feature에서 공통 사용.
- **명확한 경계**: 모듈 경계가 곧 팀원 간 작업 경계가 되어 병렬 개발 용이.

---

## 5. 모듈별 상세 명세 (13개)

### [1] `:app`
| 항목 | 내용 |
|---|---|
| 타입 | Application |
| 책임 | 앱 진입점, 전체 모듈 통합, 네비게이션 호스트 |
| 주요 기능 | 하단 탭바(홈·카테고리·검색·장바구니·마이), 스플래시, 전역 Navigation Graph, Hilt Application 설정 |
| 주요 구성요소 | `MainActivity`, `AppNavHost`, `BottomNavBar`, `NiaApplication(@HiltAndroidApp)` |
| 의존 모듈 | 모든 `feature:*` |

### [2] `:core:model`
| 항목 | 내용 |
|---|---|
| 타입 | Kotlin/Android Library (순수 모델) |
| 책임 | 앱 전역에서 쓰는 데이터 클래스 정의 (프레임워크 의존성 최소) |
| 주요 구성요소 | `Product`, `ProductOption`, `Category`, `CartItem`, `Order`, `OrderStatus`, `User`, `Review`, `WishItem` |
| 의존 모듈 | 없음 (최하위 계층) |

### [3] `:core:common`
| 항목 | 내용 |
|---|---|
| 타입 | Android Library |
| 책임 | 공통 유틸리티·확장함수·상태 래퍼 |
| 주요 기능 | 가격 포맷(₩12,900), 날짜 포맷, `Result`/`UiState` 래퍼, Coroutine Dispatcher 제공 |
| 주요 구성요소 | `PriceFormatter`, `UiState<T>`, `DispatcherProvider` |
| 의존 모듈 | `core:model` |

### [4] `:core:designsystem`
| 항목 | 내용 |
|---|---|
| 타입 | Android Library (Compose) |
| 책임 | 앱 전역 디자인 시스템 — 테마·색상·타이포그래피·재사용 컴포넌트 |
| 주요 기능 | 라이트/다크 테마, 색상 팔레트, 공용 버튼/뱃지/별점/로딩/에러 컴포넌트, `ProductCard`, `PriceTag` |
| 주요 구성요소 | `AppTheme`, `AppButton`, `RatingBar`, `ProductCard`, `NetworkImage` |
| 의존 모듈 | `core:model` |

### [5] `:core:data`
| 항목 | 내용 |
|---|---|
| 타입 | Android Library |
| 책임 | 데이터 계층 단일 진입점 — Repository로 네트워크·로컬을 결합 |
| 주요 기능 | 상품·주문·장바구니·찜·리뷰 Repository, Retrofit API + DTO 매핑, Room DB(장바구니·찜·최근 본 상품), DataStore(토큰·설정) |
| 주요 구성요소 | `ProductRepository`, `CartRepository`, `OrderRepository`, `AppDatabase`, `ApiService`, `UserPreferences` |
| 의존 모듈 | `core:model`, `core:common` |

### [6] `:feature:auth`
| 항목 | 내용 |
|---|---|
| 책임 | 로그인·회원가입·자동 로그인 |
| 주요 화면 | 로그인, 회원가입, 비밀번호 찾기 |
| 주요 기능 | 입력 유효성 검사, 토큰 저장(DataStore), 로그인 상태 유지 |
| 주요 구성요소 | `LoginScreen`, `SignUpScreen`, `AuthViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [7] `:feature:home`
| 항목 | 내용 |
|---|---|
| 책임 | 홈 피드·추천·기획전 |
| 주요 화면 | 홈(배너, 랭킹, 추천 상품 그리드) |
| 주요 기능 | 이미지 배너 캐러셀, 상품 목록 페이징, 최근 본 상품 기반 추천 |
| 주요 구성요소 | `HomeScreen`, `HomeViewModel`, `BannerPager` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [8] `:feature:category`
| 항목 | 내용 |
|---|---|
| 책임 | 카테고리 기반 상품 탐색 |
| 주요 화면 | 카테고리 목록, 카테고리별 상품 목록 |
| 주요 기능 | 대/중분류 탐색, 카테고리별 필터링 |
| 주요 구성요소 | `CategoryScreen`, `CategoryProductListScreen`, `CategoryViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [9] `:feature:search`
| 항목 | 내용 |
|---|---|
| 책임 | 검색 및 필터·정렬 |
| 주요 화면 | 검색 입력, 검색 결과, 필터 시트 |
| 주요 기능 | 키워드 검색, 최근 검색어(Room), 브랜드·가격·사이즈 필터, 인기순/가격순/최신순 정렬 |
| 주요 구성요소 | `SearchScreen`, `SearchResultScreen`, `FilterBottomSheet`, `SearchViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [10] `:feature:productdetail`
| 항목 | 내용 |
|---|---|
| 책임 | 상품 상세 정보·옵션 선택·리뷰 요약 |
| 주요 화면 | 상품 상세 |
| 주요 기능 | 이미지 갤러리, 색상·사이즈 옵션 선택, 리뷰 요약·포토리뷰, 장바구니 담기, 찜하기 |
| 주요 구성요소 | `ProductDetailScreen`, `OptionBottomSheet`, `ProductDetailViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [11] `:feature:cart`
| 항목 | 내용 |
|---|---|
| 책임 | 장바구니 관리 |
| 주요 화면 | 장바구니 |
| 주요 기능 | 담긴 상품 목록, 수량·옵션 변경, 선택 삭제, 쿠폰·적립 계산, 선택 상품 주문 |
| 주요 구성요소 | `CartScreen`, `CartViewModel`, `PriceSummary` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [12] `:feature:order`
| 항목 | 내용 |
|---|---|
| 책임 | 주문서 작성·결제(시뮬레이션)·주문 완료 |
| 주요 화면 | 주문서, 결제, 주문 완료 |
| 주요 기능 | 배송지 입력, 결제수단 선택, 최종 금액 계산, 결제 완료 시뮬레이션, 주문 생성 |
| 주요 구성요소 | `OrderScreen`, `PaymentScreen`, `OrderCompleteScreen`, `OrderViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

### [13] `:feature:mypage`
| 항목 | 내용 |
|---|---|
| 책임 | 마이페이지 — 주문내역·찜·리뷰·설정 |
| 주요 화면 | 마이페이지, 주문내역·배송조회, 찜 목록, 내 리뷰, 설정 |
| 주요 기능 | 주문 상태 타임라인, 찜 목록 관리, 리뷰 작성·조회, 다크모드 토글 |
| 주요 구성요소 | `MyPageScreen`, `OrderHistoryScreen`, `WishlistScreen`, `SettingsScreen`, `MyPageViewModel` |
| 의존 모듈 | `core:data`, `core:designsystem`, `core:common` |

---

## 6. 벤치마킹 대비 개선·추가 기능

원본 무신사 대비 개선점을 명확히 하여 발표 시 Before/After 비교가 가능하도록 한다.

| # | 기능 | 설명 | 관련 모듈 |
|---|---|---|---|
| 1 | 찜 가격 변동 알림 | 찜한 상품이 세일하면 로컬 알림(Notification) 발송 | mypage, data |
| 2 | 최근 본 상품 기반 추천 | 규칙 기반으로 "이런 상품 어때요" 추천 노출 | home, data |
| 3 | 장바구니 쿠폰·적립 계산기 | 실시간 최종 결제 금액 계산 UI | cart |
| 4 | 상품 비교 기능 | 2~3개 상품 스펙(가격·사이즈·평점) 나란히 비교 | productdetail |
| 5 | 배송 상태 타임라인 UI | 주문→결제→배송중→완료를 시각적 타임라인으로 표현 | mypage |
| 6 | 다크모드 + 접근성 개선 | 라이트/다크 테마 전환, 폰트 크기·명암 대비 개선 | designsystem, mypage |

---

## 7. 모듈별 다뤄야 할 핵심 기술

### 7.1 아키텍처 공통
- **MVVM 패턴**: View(Compose) ↔ ViewModel ↔ Repository 분리
- **단방향 데이터 흐름(UDF)**: ViewModel이 `StateFlow`로 UI 상태를 노출, UI는 이벤트만 위로 전달
- **의존성 주입(Hilt)**: 모듈 간 결합도를 낮추고 테스트 용이성 확보

### 7.2 핵심 기술 난관 및 대응
커머스 앱은 **상태가 여러 화면에 걸쳐 공유되는 것**이 핵심 난이도다.

- **장바구니 상태 공유**: 상품상세에서 담기 → 하단 탭 장바구니 뱃지 갱신 → 주문서로 전달. `CartRepository`를 단일 진실 공급원(Single Source of Truth)으로 두어 모든 화면이 같은 상태를 구독한다.
- **주문 흐름의 상태 전이**: 장바구니 → 주문서 → 결제 → 완료의 데이터 전달 방식(Navigation argument vs Repository 공유)을 초반에 확정한다.
- **옵션 선택 로직**: 색상·사이즈 조합에 따른 재고·가격 변동 처리.
- **페이징**: 상품 목록의 무한 스크롤은 Paging 3 또는 수동 페이징으로 구현.

### 7.3 데이터 계층
- **Room**: 장바구니·찜·최근 본 상품·최근 검색어 로컬 영속화
- **DataStore**: 로그인 토큰, 다크모드 설정
- **Retrofit/Firebase**: 상품·주문 데이터 원격 처리, DTO ↔ 도메인 모델 매핑
- **Mock 설계**: Firebase 미사용 시 상품 JSON을 체계적으로 설계하면 개발 속도가 빨라진다.

---