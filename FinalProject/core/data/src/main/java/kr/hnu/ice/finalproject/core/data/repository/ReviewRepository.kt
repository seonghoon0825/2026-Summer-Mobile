package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.remote.AssetReader
import kr.hnu.ice.finalproject.core.data.remote.dto.ReviewDto
import kr.hnu.ice.finalproject.core.data.remote.dto.toDomain
import kr.hnu.ice.finalproject.core.model.Review
import javax.inject.Inject
import javax.inject.Singleton

/** 상품별 리뷰를 제공하는 저장소. (지금은 assets JSON) */
interface ReviewRepository {
    fun getReviewsByProduct(productId: String): Flow<List<Review>>
}

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
    private val json: Json,
    private val dispatchers: DispatcherProvider,
) : ReviewRepository {

    private val mutex = Mutex()
    private var cached: List<Review>? = null

    private suspend fun loadAll(): List<Review> = mutex.withLock {
        cached ?: run {
            val text = assetReader.readText(FILE_REVIEWS)
            json.decodeFromString<List<ReviewDto>>(text).map { it.toDomain() }
                .also { cached = it }
        }
    }

    override fun getReviewsByProduct(productId: String): Flow<List<Review>> = flow {
        emit(loadAll().filter { it.productId == productId })
    }.flowOn(dispatchers.io)

    private companion object {
        const val FILE_REVIEWS = "reviews.json"
    }
}