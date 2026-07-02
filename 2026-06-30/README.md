# 오늘의 학습 정리 (2026-07-02)

이 노트북은 Kotlin의 클래스와 함수(특히 람다, 고차함수) 사용법을 실습한 내용입니다.

## 요약
- 클래스 상속과 다형성: Shape, Circle, Rect 등으로 상속/오버라이드, 리스트 순회 시 각 객체의 draw() 호출
- 타입 검사 및 캐스팅: is, as 사용 예시(사각형 검사 후 info() 호출)
- when을 이용한 타입 분기: 객체 타입에 따라 분기 출력
- 추상 클래스: Animal1/Animal2 예시, 추상 메서드와 오버라이드
- 인터페이스: Flyable과 Duck 구현(추상 메서드 + 기본 메서드)
- 클래스 위임(Delegation): Printer 인터페이스, ColorPrinter, Logger2(Printer by printer) 예시
- 람다 및 고차함수: 단순 람다(double), 클로저(multiplier), 반환함수 예시(testFun)

## 주요 코드 포인트
- 상속: open/override 사용
- 추상: abstract class와 abstract fun
- 인터페이스: 기본 메서드(default implementation)
- 위임: "by" 키워드로 인터페이스 위임
- 람다: (Int) -> Int 타입, it 예약어, 고차 함수 반환

필요하면 각 섹션에 코드 예제를 추가해 정리해 드립니다.