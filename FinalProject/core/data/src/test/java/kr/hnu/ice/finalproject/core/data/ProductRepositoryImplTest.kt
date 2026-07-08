package kr.hnu.ice.finalproject.core.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.RecentProductDao
import kr.hnu.ice.finalproject.core.data.local.entity.RecentProductEntity
import kr.hnu.ice.finalproject.core.data.remote.AssetReader
import kr.hnu.ice.finalproject.core.data.repository.ProductRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * ProductRepositoryImpl이 실제 assets JSON(Mock)에서 상품을 로드하는지 검증한다.
 * 디바이스 없이 JVM에서 실행되도록 AssetReader를 파일 시스템 fake로 대체했다.
 */
class ProductRepositoryImplTest {

    // 실제 src/main/assets 파일을 읽는 fake (테스트 실행 디렉터리 = 모듈 루트)
    private class FakeAssetReader : AssetReader {
        override suspend fun readText(fileName: String): String =
            File("src/main/assets/$fileName").readText()
    }

    // 테스트에서는 스레드 전환 없이 동작하도록 Unconfined 사용
    private class TestDispatcherProvider : DispatcherProvider {
        private val d: CoroutineDispatcher = Dispatchers.Unconfined
        override val main = d
        override val io = d
        override val default = d
    }

    // getProducts 테스트에는 쓰이지 않는 no-op DAO
    private class NoOpRecentProductDao : RecentProductDao {
        override fun observeRecent(limit: Int): Flow<List<RecentProductEntity>> = flowOf(emptyList())
        override suspend fun upsert(item: RecentProductEntity) = Unit
        override suspend fun delete(productId: String) = Unit
        override suspend fun clear() = Unit
    }

    private fun createRepository() = ProductRepositoryImpl(
        assetReader = FakeAssetReader(),
        json = Json { ignoreUnknownKeys = true },
        dispatchers = TestDispatcherProvider(),
        recentProductDao = NoOpRecentProductDao(),
    )

    @Test
    fun `상품 목록이 20개 이상 로드된다`() = runTest {
        val products = createRepository().getProducts().first()
        assertTrue("상품이 20개 이상이어야 함 (실제=${products.size})", products.size >= 20)
    }

    @Test
    fun `상품 필드와 카테고리 매핑이 올바르다`() = runTest {
        val repo = createRepository()
        val p1 = repo.getProductById("p1")
        assertNotNull(p1)
        requireNotNull(p1)
        assertEquals("어센틱 로고 스웨트셔츠", p1.name)
        assertEquals("커버낫", p1.brand)
        assertEquals(59000, p1.price)
        // categoryId="top" 이 실제 Category(name="상의")로 매핑되어야 한다
        assertEquals("top", p1.category.id)
        assertEquals("상의", p1.category.name)
        assertTrue("옵션이 존재해야 함", p1.options.isNotEmpty())
    }

    @Test
    fun `카테고리별 필터링이 동작한다`() = runTest {
        val shoes = createRepository().getProductsByCategory("shoes").first()
        assertTrue("신발 카테고리 상품이 있어야 함", shoes.isNotEmpty())
        assertTrue("모두 shoes 카테고리여야 함", shoes.all { it.category.id == "shoes" })
    }

    @Test
    fun `브랜드명으로 검색된다`() = runTest {
        val result = createRepository().searchProducts("커버낫").first()
        assertTrue("검색 결과가 있어야 함", result.isNotEmpty())
        assertTrue("모두 커버낫 브랜드여야 함", result.all { it.brand == "커버낫" })
    }
}