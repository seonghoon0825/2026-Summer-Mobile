package kr.hnu.ice.finalproject.core.common.util

/**
 * 가격(원)을 화면 표기용 문자열로 변환하는 유틸.
 *
 * 예) 12900 -> "₩12,900"
 */
object PriceFormatter {

    /**
     * 정수 가격을 천 단위 콤마 + 원화 기호(₩)가 붙은 문자열로 변환한다.
     *
     * @param price 원(KRW) 단위의 정수 가격
     * @return 예: "₩12,900" (음수도 "-₩1,000" 형태로 처리)
     */
    fun format(price: Int): String {
        val grouped = "%,d".format(kotlin.math.abs(price))
        val sign = if (price < 0) "-" else ""
        return "$sign₩$grouped"
    }
}