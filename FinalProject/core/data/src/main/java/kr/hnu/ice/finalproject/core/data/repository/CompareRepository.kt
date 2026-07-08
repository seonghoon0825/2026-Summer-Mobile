package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 비교함. 사용자가 비교하려고 담은 상품 id 목록을 관리한다. (인메모리, 최대 [MAX_COMPARE]개)
 * 여러 화면(상세, 비교 화면)에서 공유하므로 단일 소스로 둔다.
 */
interface CompareRepository {
    fun observeSelectedIds(): Flow<List<String>>

    /**
     * 담기/빼기 토글. 이미 담겼으면 제거하고, 아니면 추가한다(최대치 초과 시 추가하지 않음).
     * @return 토글 후 담긴 상태면 true, 최대치 초과로 담지 못했으면 false
     */
    suspend fun toggle(productId: String): Boolean

    suspend fun remove(productId: String)
    suspend fun clear()

    companion object {
        const val MAX_COMPARE = 3
    }
}

@Singleton
class CompareRepositoryImpl @Inject constructor() : CompareRepository {

    private val selectedIds = MutableStateFlow<List<String>>(emptyList())

    override fun observeSelectedIds(): Flow<List<String>> = selectedIds.asStateFlow()

    override suspend fun toggle(productId: String): Boolean {
        val current = selectedIds.value
        return when {
            productId in current -> {
                selectedIds.value = current - productId
                false
            }
            current.size >= CompareRepository.MAX_COMPARE -> false // 최대치 초과: 담지 않음
            else -> {
                selectedIds.value = current + productId
                true
            }
        }
    }

    override suspend fun remove(productId: String) {
        selectedIds.value = selectedIds.value - productId
    }

    override suspend fun clear() {
        selectedIds.value = emptyList()
    }
}