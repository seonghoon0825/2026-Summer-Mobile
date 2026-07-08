# July08Application

날짜: 2026-07-08

요약
- 간단한 계산기 앱 예제(액티비티 간 데이터 전달 및 결과 반환)를 학습함.

모듈별 학습 내용

1) MainActivity
- ViewBinding(ActivityMainBinding) 사용으로 뷰 참조 간소화
- ActivityResultLauncher와 ActivityResultContracts.StartActivityForResult를 사용해 CalcActivity를 호출하고 결과 수신
- Intent에 num1, num2, op 값을 putExtra로 전달
- 수신된 결과를 TextView에 표시하고 색상으로 상태(정상/취소)를 표시

2) CalcActivity
- Intent로 전달된 num1, num2, op 값을 getDoubleExtra/getStringExtra로 읽음
- when 식으로 연산 처리(+, -, *, /) 및 0으로 나눌 경우 null 처리
- 계산 결과가 유효하면 setResult(RESULT_OK, intent)로 결과 전달, 그렇지 않으면 RESULT_CANCELED와 메시지 전달
- 계산 완료 후 finish() 호출

3) 레이아웃(activity_main.xml, activity_calc.xml)
- activity_main: EditText(num1, num2), RadioGroup(연산 선택), 계산 버튼(sendBtn), 결과 표시용 TextView
- activity_calc: 전달받은 데이터 표시용 TextView, 결과(정상/오류) 반환 버튼
- 입력 타입(numberDecimal), 레이아웃 구성(LinearLayout, RadioButton) 사용

4) 리소스
- strings.xml에 앱 이름 정의
- themes, colors 등 기본 UI 리소스 포함

핵심 학습 포인트
- Activity 간 데이터 전달과 결과 처리 흐름 이해
- ViewBinding 사용법과 안전한 널 처리
- Intent extras와 결과 코드(RESULT_OK/CANCELED) 활용
- 간단한 입력 검증(0으로 나누기 처리) 및 UI 상태 반영

실행 방법
- Android Studio로 프로젝트 열기 → 앱 모듈을 실행(에뮬레이터 또는 기기)

---
(자동 생성)