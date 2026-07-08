package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentProductDao
import kr.hnu.ice.finalproject.core.data.local.entity.RecentProductEntity
import kr.hnu.ice.finalproject.core.data.remote.AssetReader
import kr.hnu.ice.finalproject.core.data.remote.dto.CategoryDto
import kr.hnu.ice.finalproject.core.data.remote.dto.ProductDto
import kr.hnu.ice.finalproject.core.data.remote.dto.toDomain
import kr.hnu.ice.finalproject.core.model.Category
import kr.hnu.ice.finalproject.core.model.Product
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 상품/카테고리/최근 본 상품을 제공하는 저장소.
 *
 * 지금은 assets JSON(Mock)에서 읽지만, 이 인터페이스만 알고 있는 상위 계층은
 * 나중에 서버 API로 교체돼도 코드가 바뀌지 않는다.
 */
interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getCategories(): Flow<List<Category>>

    /** 최근 본 상품으로 기록. */
    suspend fun addRecentViewed(productId: String)

    /** 최근 본 상품 목록(최신순). */
    fun getRecentViewedProducts(): Flow<List<Product>>

    /**
     * 최근 본 상품 기반 개인화 추천(최신순).
     * 최근 본 상품들의 카테고리를 모아 같은 카테고리의 다른 상품을 반환하며,
     * 이미 본 상품과 장바구니에 담긴 상품은 제외한다. 최근 본 게 없으면 빈 목록.
     */
    fun getRecommendations(): Flow<List<Product>>
}

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
    private val json: Json,
    private val dispatchers: DispatcherProvider,
    private val recentProductDao: RecentProductDao,
    private val cartDao: CartDao,
) : ProductRepository {

    // assets는 변하지 않으므로 한 번 파싱한 뒤 메모리에 캐시한다.
    private val mutex = Mutex()
    private var cachedProducts: List<Product>? = null
    private var cachedCategories: List<Category>? = null

    private suspend fun loadCategories(): List<Category> = mutex.withLock {
        cachedCategories ?: run {
            val text = assetReader.readText(FILE_CATEGORIES)
            val dto = json.decodeFromString<List<CategoryDto>>(text)
            dto.map { it.toDomain() }.also { cachedCategories = it }
        }
    }

    private suspend fun loadProducts(): List<Product> {
        // 카테고리를 먼저 로드(락 밖에서)한 뒤 상품 파싱 시 매핑에 사용한다.
        val categories = loadCategories()
        return mutex.withLock {
            cachedProducts ?: run {
                val categoryMap = categories.associateBy { it.id }
                val text = assetReader.readText(FILE_PRODUCTS)
                val dto = json.decodeFromString<List<ProductDto>>(text)
                dto.map { it.toDomain(categoryMap) }.also { cachedProducts = it }
            }
        }
    }

    override fun getProducts(): Flow<List<Product>> = flow {
        emit(loadProducts())
    }.flowOn(dispatchers.io)

    override suspend fun getProductById(id: String): Product? =
        loadProducts().firstOrNull { it.id == id }

    override fun getProductsByCategory(categoryId: String): Flow<List<Product>> = flow {
        emit(loadProducts().filter { it.category.id == categoryId })
    }.flowOn(dispatchers.io)

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        val keyword = query.trim()
        val result = if (keyword.isBlank()) {
            emptyList()
        } else {
            loadProducts().filter {
                it.name.contains(keyword, ignoreCase = true) ||
                    it.brand.contains(keyword, ignoreCase = true)
            }
        }
        emit(result)
    }.flowOn(dispatchers.io)

    override fun getCategories(): Flow<List<Category>> = flow {
        emit(loadCategories())
    }.flowOn(dispatchers.io)

    override suspend fun addRecentViewed(productId: String) {
        recentProductDao.upsert(
            RecentProductEntity(productId = productId, viewedAt = System.currentTimeMillis()),
        )
        // 최신 20개만 유지(초과분 삭제).
        recentProductDao.trimToLimit(RECENT_LIMIT)
    }

    override fun getRecentViewedProducts(): Flow<List<Product>> =
        recentProductDao.observeRecent(RECENT_LIMIT)
            .map { entities -> entities.mapNotNull { getProductById(it.productId) } }
            .flowOn(dispatchers.io)

    override fun getRecommendations(): Flow<List<Product>> =
        combine(
            recentProductDao.observeRecent(RECENT_LIMIT),
            cartDao.observeProductIds(),
        ) { recentEntities, cartIds ->
            if (recentEntities.isEmpty()) return@combine emptyList()

            val allProducts = loadProducts()
            val productMap = allProducts.associateBy { it.id }
            // 최근 본 상품(최신순). 카탈로그에서 사라진 id는 건너뛴다.
            val recentProducts = recentEntities.mapNotNull { productMap[it.productId] }
            if (recentProducts.isEmpty()) return@combine emptyList()

            // 이미 본 상품 + 장바구니 상품은 추천에서 제외.
            val excludeIds = recentProducts.mapTo(mutableSetOf()) { it.id }.apply { addAll(cartIds) }
            // 최근 본 순서대로 카테고리 우선순위를 두고 같은 카테고리의 다른 상품을 모은다.
            recentProducts.map { it.category.id }.distinct()
                .flatMap { categoryId ->
                    allProducts.filter { it.category.id == categoryId && it.id !in excludeIds }
                }
                .distinctBy { it.id }
                .take(RECOMMENDATION_LIMIT)
        }.flowOn(dispatchers.io)

    private companion object {
        const val FILE_PRODUCTS = "products.json"
        const val FILE_CATEGORIES = "categories.json"
        const val RECENT_LIMIT = 20
        const val RECOMMENDATION_LIMIT = 10
    }
}