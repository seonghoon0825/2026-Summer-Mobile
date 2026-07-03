# JulyFirstApplication - 학습 기록 및 요약

날짜: 2026-07-03

요약
본 리포지토리는 안드로이드 모듈별 실습 프로젝트들의 모음입니다. 오늘(2026-07-03) 진행한 학습 내용을 각 모듈별로 정리했습니다. 주요 실습 주제는 XML 레이아웃, View Attributes(패딩/인셋), View Binding 사용법, 뷰 가시성 처리, 그리고 프로그램적 UI 구성입니다.

체크리스트
- [x] 각 모듈의 목적 및 주요 학습 내용 파악
- [x] `viewbindingexam` 모듈에 ViewBinding 활성화 (빌드 설정 수정)
- [x] 빌드 및 재검증 방법 안내 제공

모듈별 학습 요약

1) app
- 패키지: `kr.hnu.ice.julyfirstapplication`
- 주요 학습: 프로그램적으로 UI를 구성하는 방법(LinearLayout, TextView, ImageView 등). 예제에서는 코드로 직접 레이아웃을 만들고 리소스(`R.drawable.moraine_lake`)를 사용해 이미지를 표시합니다.
- 참고 파일: `app/src/main/java/.../MainActivity.kt`

2) lunchmenu
- 패키지: `kr.hnu.ice.lunchmenu`
- 주요 학습: XML 레이아웃 사용 및 edge-to-edge 처리와 `WindowInsets`를 이용한 패딩 적용. 액티비티는 `setContentView(R.layout.activity_main)`을 사용합니다.
- 참고 파일: `lunchmenu/src/main/java/.../MainActivity.kt`

3) viewattributes
- 패키지: `kr.hnu.ice.viewattributes`
- 주요 학습: 뷰 속성(레이아웃 속성, 마진/패딩)과 WindowInsets 적용 실습. `enableEdgeToEdge()` 사용으로 UI가 시스템 바 영역까지 확장되는 처리 연습.

4) viewbindingexam
- 패키지: `kr.hnu.ice.viewbindingexam`
- 주요 학습: View Binding 적용과 사용법. `ActivityMainBinding.inflate(layoutInflater)`로 바인딩을 얻고 버튼 클릭으로 TextView 내용을 변경하는 예제를 포함합니다.
- 조치: 이 모듈에 `build.gradle.kts` 내 `buildFeatures { viewBinding = true }` 를 추가하여 `ActivityMainBinding` 생성 문제를 해결했습니다.
- 참고 파일: `viewbindingexam/build.gradle.kts`, `viewbindingexam/src/main/java/.../MainActivity.kt`

5) visibleclick
- 패키지: `kr.hnu.ice.visibleclick`
- 주요 학습: 버튼 클릭에 따라 다른 뷰의 `visibility`를 변경하는 실습(VISIBLE, INVISIBLE, GONE). 뷰 참조 후 리스너로 상태 변경을 구현합니다.

6) xmllayout
- 패키지: `kr.hnu.ice.xmllayout`
- 주요 학습: 가장 기본적인 XML 레이아웃 사용 예제와 edge-to-edge 처리. 단순 `setContentView` 기반 액티비티 구조 연습.

빌드 및 테스트 방법
PowerShell에서 프로젝트 루트로 이동한 뒤 다음 명령으로 클린 빌드 및 모듈 단위 빌드를 실행하세요:

```powershell
Set-Location 'C:\Users\jsh42\AndroidStudioProjects\2026-IPP\JulyFirstApplication'
.\gradlew.bat clean assembleDebug
# 또는 특정 모듈만 빌드
.\gradlew.bat :viewbindingexam:assembleDebug
```

문제 해결 팁
- View Binding 관련 문제는 해당 모듈의 Gradle 설정에 `viewBinding` 또는 `buildFeatures.viewBinding = true` 설정이 되어있는지 확인하세요(모듈별로 설정 필요).
- 레이아웃 이름이 여러 모듈에서 중복되면 IDE 자동완성이나 참조 시 혼동이 생길 수 있으니, 필요하면 모듈 고유의 파일명 정책을 적용하세요.
- 빌드 후에도 문제가 남으면 Android Studio에서 `File → Invalidate Caches / Restart`를 시도하세요.

향후 할 일(권장)
- 다른 모듈에도 ViewBinding 활성화가 필요하면 동일 설정을 적용
- 각 모듈의 README 확장(실습 목표, 학습 포인트, 개선 아이디어)

작성자: 자동 요약 스크립트

