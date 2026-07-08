package kr.hnu.ice.finalproject.core.common.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 코루틴 Dispatcher를 추상화한 인터페이스.
 *
 * Dispatchers를 직접 쓰지 않고 이 인터페이스로 주입받으면,
 * 테스트에서 TestDispatcher로 손쉽게 교체할 수 있다.
 */
interface DispatcherProvider {
    /** UI 갱신용 (메인 스레드) */
    val main: CoroutineDispatcher

    /** I/O 작업용 (네트워크/디스크) */
    val io: CoroutineDispatcher

    /** CPU 집약 연산용 */
    val default: CoroutineDispatcher
}

/**
 * 실제 앱에서 사용하는 기본 구현. kotlinx의 Dispatchers를 그대로 위임한다.
 * (Hilt 바인딩은 상위 계층(core:data 등)의 Hilt Module에서 제공한다)
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
