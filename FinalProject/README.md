# FinalProject — 무신사 벤치마킹 쇼핑 앱

무신사(MUSINSA) 앱을 벤치마킹하여 Jetpack Compose + 멀티모듈 구조로 만든 패션 쇼핑 앱입니다.
홈/카테고리/검색/상품상세/장바구니/주문/마이페이지의 핵심 쇼핑 흐름을 구현하고,
원본 앱 대비 6가지 기능을 추가·개선했습니다.

## 벤치마킹 대비 개선 기능 6가지

| # | 기능 | 개선 내용 (Before → After) |
|---|------|---------------------------|
| 1 | 찜 가격 알림 | 찜만 가능했던 위시리스트 → 찜 상품 가격 인하 시 로컬 푸시 알림 발송 (설정 화면에서 시연 가능) |
| 2 | 최근 본 상품 기반 추천 | 단순 최근 본 목록 → 최근 본 상품과 같은 카테고리의 상품을 홈에 "비슷한 상품"으로 개인화 추천 |
| 3 | 쿠폰·적립 계산기 | 결제 직전에야 보이는 할인 → 장바구니에서 쿠폰(정액/정률/최소주문/최대할인)·배송비·적립 예정 포인트(1%)를 실시간 계산 |
| 4 | 상품 비교 | 탭을 오가며 눈으로 비교 → 최대 3개 상품을 비교함에 담아 가격/평점/리뷰/옵션을 한 화면에서 나란히 비교 |
| 5 | 배송 상태 타임라인 | 텍스트 한 줄 상태 → 주문내역에서 주문완료→결제완료→배송중→배송완료 4단계 시각화 타임라인 |
| 6 | 다크모드 + 접근성 | 시스템 설정 의존 → 앱 내 다크모드 토글 + 글자 크기 조절 + 고대비 모드(WCAG 대비 강화), DataStore에 영구 저장 |

## 모듈 구조 (총 13개 모듈)

```
:app                    앱 진입점 (MainActivity, 내비게이션, 알림)
:core:model             도메인 모델 (Product, Order, CartItem …)
:core:common            공용 유틸 (UiState, PriceFormatter …)
:core:designsystem      디자인 시스템 (테마, 색상, 공용 컴포넌트)
:core:data              데이터 계층 (Repository, Room, DataStore, Mock JSON)
:feature:auth           로그인 / 회원가입
:feature:home           홈 (배너, 랭킹, 개인화 추천, 최근 본 상품)
:feature:category       카테고리 / 카테고리별 상품 목록
:feature:search         검색 + 필터
:feature:productdetail  상품 상세 / 상품 비교
:feature:cart           장바구니 (쿠폰·적립 계산)
:feature:order          주문서 / 결제 / 주문 완료
:feature:mypage         마이페이지 (찜, 주문내역, 리뷰, 설정)
```

- 의존 방향: `app → feature → core` (feature 간 직접 의존 없음, core는 아래로만 의존)

## 기술 스택

- **UI**: Jetpack Compose (Material3, BOM 2025.09), Navigation Compose
- **아키텍처**: 멀티모듈 + MVVM (ViewModel + StateFlow + UiState)
- **DI**: Hilt
- **로컬 저장소**: Room(장바구니·찜·최근 본 상품), DataStore(테마·접근성 설정)
- **이미지**: Coil 3 (Unsplash 상품 이미지)
- **데이터**: assets의 Mock JSON (products.json, reviews.json) — Repository 계층이 출처를 은닉

## 빌드 & 실행

- **요구 사항**: Android Studio (AGP 9.2.1, Kotlin 2.2.10 내장), JDK 17+, minSdk 기기/에뮬레이터
- Android Studio에서 프로젝트를 열고 `app` 구성으로 Run 하거나:

```bash
./gradlew assembleDebug     # 빌드
./gradlew test              # 단위 테스트
```

## 주요 시연 순서 (개선 기능 중심)

1. 홈에서 상품 2~3개 열람 → 홈 복귀 → **"최근 본 상품과 비슷한 상품"** 추천 섹션 확인
2. 상품 상세에서 **비교함 담기** 2~3개 → 비교 화면에서 스펙 나란히 비교
3. 장바구니에 담기 → **쿠폰 선택/해제**하며 할인·배송비·적립 포인트 실시간 변동 확인
4. 주문 완료 → 마이페이지 주문내역에서 **배송 타임라인** + "배송 단계 진행" 버튼으로 상태 전환
5. 상품 찜 → 설정에서 **"찜 상품 가격 인하 알림"** 발송 → 로컬 알림 확인
6. 설정에서 **다크모드 / 글자 크기 / 고대비** 전환 → 전체 화면 즉시 반영 확인