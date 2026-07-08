package kr.hnu.ice.finalproject.core.common

/**
 * 화면 UI 상태를 표현하는 공통 래퍼.
 * ViewModel이 StateFlow<UiState<T>> 형태로 노출해 단방향 데이터 흐름(UDF)을 만든다.
 *
 * - [Loading] : 로딩 중
 * - [Success] : 성공 (데이터 포함)
 * - [Error]   : 실패 (메시지 포함)
 */
sealed interface UiState<out T> {

    /** 로딩 중 상태. 데이터가 없으므로 UiState<Nothing>. */
    data object Loading : UiState<Nothing>

    /** 성공 상태. 실제 데이터를 담는다. */
    data class Success<T>(val data: T) : UiState<T>

    /** 에러 상태. 사용자에게 보여줄 메시지와(선택) 원인 예외를 담는다. */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : UiState<Nothing>
}