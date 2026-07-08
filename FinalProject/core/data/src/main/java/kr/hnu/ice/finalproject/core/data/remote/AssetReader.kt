package kr.hnu.ice.finalproject.core.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock 데이터(assets JSON)를 읽어오는 추상화.
 *
 * 이 인터페이스가 "데이터 출처"를 감추는 seam이다.
 * 나중에 서버를 붙이면 이 자리에 Retrofit ApiService 기반 구현을 끼우고,
 * Repository 인터페이스와 상위 계층 코드는 그대로 둘 수 있다.
 */
interface AssetReader {
    /** assets 루트 기준 파일명을 읽어 문자열로 반환한다. */
    suspend fun readText(fileName: String): String
}

/** Android assets 폴더에서 파일을 읽는 기본 구현. */
@Singleton
class AndroidAssetReader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AssetReader {
    override suspend fun readText(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }
}