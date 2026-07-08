package kr.hnu.ice.finalproject.core.model

/**
 * 접근성용 글자 크기 옵션. 앱 타이포그래피 전체에 곱해지는 배율을 갖는다.
 *
 * @param scale 기준(NORMAL) 대비 글자 크기 배율
 */
enum class FontSizeOption(val scale: Float) {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.15f),
    EXTRA_LARGE(1.3f),
}