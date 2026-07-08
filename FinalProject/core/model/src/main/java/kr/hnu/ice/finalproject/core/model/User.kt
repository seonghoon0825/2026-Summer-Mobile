package kr.hnu.ice.finalproject.core.model

/**
 * 사용자 계정 정보.
 *
 * @param id 사용자 식별자
 * @param name 사용자 이름/닉네임
 * @param email 이메일
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
)