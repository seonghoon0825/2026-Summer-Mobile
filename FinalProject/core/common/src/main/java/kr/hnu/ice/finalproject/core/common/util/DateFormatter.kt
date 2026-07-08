package kr.hnu.ice.finalproject.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 날짜 포맷 유틸. epoch milliseconds(Long)를 화면 표기용 문자열로 변환한다.
 *
 * minSdk 24를 지원해야 하므로(java.time은 API 26+ 필요) SimpleDateFormat을 사용한다.
 */
object DateFormatter {

    /** 기본 날짜 패턴 (예: 2026.07.07) */
    const val DATE_PATTERN: String = "yyyy.MM.dd"

    /** 날짜 + 시각 패턴 (예: 2026.07.07 14:30) */
    const val DATE_TIME_PATTERN: String = "yyyy.MM.dd HH:mm"

    /**
     * epoch milliseconds를 지정한 패턴의 문자열로 변환한다.
     *
     * @param epochMillis 밀리초 단위 시각
     * @param pattern 날짜 포맷 패턴 (기본값: [DATE_PATTERN])
     */
    fun format(epochMillis: Long, pattern: String = DATE_PATTERN): String {
        // SimpleDateFormat은 스레드 세이프하지 않으므로 호출 시마다 생성한다.
        val formatter = SimpleDateFormat(pattern, Locale.KOREA)
        return formatter.format(Date(epochMillis))
    }
}