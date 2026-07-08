package kr.hnu.ice.finalproject.core.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentProductDao
import kr.hnu.ice.finalproject.core.data.local.entity.CartItemEntity
import kr.hnu.ice.finalproject.core.data.local.entity.RecentProductEntity
import kr.hnu.ice.finalproject.core.data.remote.AssetReader
import kr.hnu.ice.finalproject.core.data.repository.ProductRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    // 최근 본 상품을 메모리에 저장하는 fake (실제 Room DAO 동작을 모사).
    private class FakeRecentProductDao(
        initial: List<RecentProductEntity> = emptyList(),
    ) : RecentProductDao {
        private val state = MutableStateFlow(initial.sortedByDescending { it.viewedAt })
        override fun observeRecent(limit: Int): Flow<List<RecentProductEntity>> =
            state.map { it.take(limit) }
        override suspend fun upsert(item: RecentProductEntity) = state.update { list ->
            (list.filterNot { it.productId == item.productId } + item)
                .sortedByDescending { it.viewedAt }
        }
        override suspend fun trimToLimit(limit: Int) = state.update {
            it.sortedByDescending { e -> e.viewedAt }.take(limit)
        }
        override suspend fun delete(productId: String) =
            state.update { it.filterNot { e -> e.productId == productId } }
        override suspend fun clear() { state.value = emptyList() }
    }

    // 장바구니 상품 id만 고정 노출하는 fake (추천 제외 검증용).
    private class FakeCartDao(private val productIds: List<String> = emptyList()) : CartDao {
        override fun observeAll(): Flow<List<CartItemEntity>> = flowOf(emptyList())
        override fun observeProductIds(): Flow<List<String>> = flowOf(productIds)
        override suspend fun upsert(item: CartItemEntity) = Unit
        override suspend fun updateQuantity(productId: String, color: String, size: String, quantity: Int) = Unit
        override suspend fun delete(productId: String, color: String, size: String) = Unit
        override suspend fun clear() = Unit
    }

    private fun createRepository(
        recentProductDao: RecentProductDao = FakeRecentProductDao(),
        cartDao: CartDao = FakeCartDao(),
    ) = ProductRepositoryImpl(
        assetReader = FakeAssetReader(),
        json = Json { ignoreUnknownKeys = true },
        dispatchers = TestDispatcherProvider(),
        recentProductDao = recentProductDao,
        cartDao = cartDao,
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

    @Test
    fun `상품 상세를 열면 같은 카테고리의 다른 상품이 추천된다`() = runTest {
        val repo = createRepository()
        val products = repo.getProducts().first()
        // 같은 카테고리에 상품이 2개 이상 있는 카테고리를 하나 고른다.
        val categoryId = products.groupBy { it.category.id }
            .entries.first { it.value.size >= 2 }.key
        val viewed = products.first { it.category.id == categoryId }

        repo.addRecentViewed(viewed.id)
        val recommendations = repo.getRecommendations().first()

        assertTrue("추천 결과가 있어야 함", recommendations.isNotEmpty())
        assertTrue("모두 같은 카테고리여야 함", recommendations.all { it.category.id == categoryId })
        assertFalse("이미 본 상품은 제외되어야 함", recommendations.any { it.id == viewed.id })
    }

    @Test
    fun `장바구니에 담긴 상품은 추천에서 제외된다`() = runTest {
        val products = createRepository().getProducts().first()
        val categoryId = products.groupBy { it.category.id }
            .entries.first { it.value.size >= 3 }.key
        val sameCategory = products.filter { it.category.id == categoryId }
        val viewed = sameCategory[0]
        val inCart = sameCategory[1]

        val repo = createRepository(cartDao = FakeCartDao(listOf(inCart.id)))
        repo.addRecentViewed(viewed.id)
        val recommendations = repo.getRecommendations().first()

        assertFalse("장바구니 상품은 제외되어야 함", recommendations.any { it.id == inCart.id })
    }

    @Test
    fun `최근 본 상품이 없으면 추천 목록은 비어 있다`() = runTest {
        assertTrue(createRepository().getRecommendations().first().isEmpty())
    }

    @Test
    fun `최근 본 상품은 최대 20개만 유지된다`() = runTest {
        val dao = FakeRecentProductDao()
        val repo = createRepository(recentProductDao = dao)
        val products = repo.getProducts().first().take(25)
        assertTrue("검증에는 21개 이상 상품이 필요", products.size >= 21)

        products.forEach { repo.addRecentViewed(it.id) }

        assertEquals(20, dao.observeRecent(100).first().size)
    }
}