# 2026년 6월 29일 학습 내용 요약

## 📚 학습 주제
배열과 for 루프, Kotlin 클래스 및 객체지향 프로그래밍

---

## 1️⃣ 배열(Array) 기초

### 배열 선언 및 초기화
- `Array<T>(size, init)` 형태로 선언, 초기값 지정 가능
- `IntArray`, `DoubleArray` 등의 원시 타입 배열도 지원
- 람다식으로 초기화: `Array(5, {i -> i * 2})`

### 배열 접근
```kotlin
val data = Array(5) {i -> i * 2}
println(data[0])       // 인덱싱
println(data.size)     // 배열 크기
println(data.get(0))   // get() 메서드
```

---

## 2️⃣ For 루프 활용

### 1. 기본 반복문
```kotlin
for (item in array) { }          // 원소 순회
for (i in array.indices) { }     // 인덱스 순회
for ((i, v) in array.withIndex()) { }  // 인덱스와 값 동시 순회
```

### 2. 배열 수정
```kotlin
for (i in array.indices) {
    array[i] *= 10  // 원소 직접 수정
}
```

### 3. 배열 출력 함수 예제
```kotlin
fun printArray(array: IntArray) {
    for ((i, v) in array.withIndex()) {
        print(v)
        if (i < array.size - 1) print(", ")
        else println()
    }
}
```

---

## 3️⃣ 컬렉션 (List, Set, Map)

### List (리스트)
- **특징**: 순서 유지, 중복 허용
- `listOf<T>()` 로 선언
- `withIndex()`로 인덱스와 값 동시 접근

### Set (집합)
- **특징**: 순서 없음, 중복 불허
- `setOf()` 로 선언
- 중복된 원소는 자동으로 제거됨

### Map (맵)
- **특징**: Key-Value 쌍으로 저장
- `mapOf()`, `Pair()` 또는 `to` 연산자로 선언
- `.keys`, `.values`, `.entries`로 접근
- 다양한 타입의 키 지원 (Int, String 등)

```kotlin
val mapData = mapOf(1 to "Mon", "two" to "Tue")
for ((k, v) in mapData.entries) {
    println("$k, $v")
}
```

---

## 4️⃣ Kotlin 클래스 (OOP)

### 기본 클래스 선언
```kotlin
class User(name: String, age: Int) {
    val name: String
    val age: Int
    
    init {
        this.name = name
        this.age = age
    }
}
```

### 간소화된 선언 (프로퍼티 직접 정의)
```kotlin
class User2(val name: String, val age: Int) {
    fun info() { println("name: $name") }
}
```

### 다양한 생성자 (Constructor Overloading)
```kotlin
class Student(val name: String, val age: Int) {
    constructor(name: String) : this(name, 0)
    constructor() : this("Unknown", 0)
}

val s1 = Student()
val s2 = Student("Hong")
val s3 = Student("Kim", 24)
```

---

## 5️⃣ 상속 (Inheritance)

### 상속 기본
- 상속 대상 클래스는 `open` 키워드 필수
- 자식 클래스는 `: SuperClass()` 형태로 상속

```kotlin
open class Super {
    var superData = 10
    fun superFun() { println("I am superFun: $superData") }
}

class Sub : Super()
val obj = Sub()
obj.superData = 20
obj.superFun()
```

### 메서드 오버라이딩 (Override)
- 부모 클래스의 메서드/프로퍼티는 `open` 키워드 필수
- 자식 클래스에서 `override` 키워드로 재정의

```kotlin
open class Super2 {
    open var superData = 10
    open fun superFun() { println("I am superFun: $superData") }
}

class Sub2 : Super2() {
    override var superData = 20
    override fun superFun() { 
        println("I am superFun of Sub2: $superData") 
    }
}
```

---

## 6️⃣ Data Class

- `data` 키워드로 선언
- 자동으로 `equals()`, `hashCode()`, `toString()` 생성
- `copy()` 메서드 지원
- 추가 생성자도 정의 가능

```kotlin
data class DataClass(val name: String, val age: Int) {
    var email: String? = null
    
    constructor(name: String, age: Int, email: String) : this(name, age) {
        this.email = email
    }
}

val obj1 = DataClass("Hong", 30, "h@hnu.kr")
val obj2 = DataClass("Hong", 30)
println(obj1.equals(obj2))  // false (email이 다름)
```

---

## 7️⃣ Companion Object (동반 객체)

- 클래스의 정적 멤버 역할
- `companion object` 블록 내의 변수/메서드는 클래스명으로 직접 접근
- 인스턴스 생성 없이 호출 가능

```kotlin
class MyClass {
    var data = 20  // 인스턴스 멤버
    
    fun some() { println("instance method") }
    
    companion object {  // 정적 멤버 역할
        var data = 10
        
        fun some() { 
            println("companion object: $data")
        }
    }
}

MyClass.data = 20  // 클래스명으로 직접 접근
MyClass.some()

val o1 = MyClass()
o1.data = 30       // 인스턴스 멤버 수정
o1.some()          // 인스턴스 메서드 호출
```

---

## 💡 주요 학습 포인트

1. **배열과 컬렉션**: 배열은 고정 크기, List/Set/Map은 동적 크기
2. **For 루프**: `withIndex()` 활용으로 인덱스와 값 동시 접근 가능
3. **OOP 기본**: 클래스 선언, 생성자 오버로딩, 상속
4. **Kotlin 특징**: 프로퍼티 간소화, `open`/`override` 명시, Data Class 자동 생성
5. **정적 멤버**: `companion object`로 클래스 레벨의 변수/메서드 관리

---

## 🎯 실습 예제
노트북에는 각 개념별로 실행 가능한 코드 예제가 포함되어 있습니다.
각 섹션의 코드를 실행하여 배열, 루프, 컬렉션, 클래스의 동작을 직접 확인할 수 있습니다.
