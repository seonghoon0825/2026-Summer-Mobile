package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
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
}

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
    private val json: Json,
    private val dispatchers: DispatcherProvider,
    private val recentProductDao: RecentProductDao,
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
    }

    override fun getRecentViewedProducts(): Flow<List<Product>> =
        recentProductDao.observeRecent(RECENT_LIMIT)
            .map { entities -> entities.mapNotNull { getProductById(it.productId) } }
            .flowOn(dispatchers.io)

    private companion object {
        const val FILE_PRODUCTS = "products.json"
        const val FILE_CATEGORIES = "categories.json"
        const val RECENT_LIMIT = 20
    }
}