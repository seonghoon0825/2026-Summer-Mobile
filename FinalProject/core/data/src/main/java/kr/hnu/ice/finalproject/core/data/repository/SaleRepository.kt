package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세일(가격 인하) 상태를 관리하는 저장소. (백엔드 없이 Mock 인메모리)
 * productId -> 세일가(원) 맵을 노출한다. 세일 중이 아닌 상품은 맵에 없다.
 */
interface SaleRepository {
    /** 현재 세일가 맵(productId -> 세일가). */
    fun observeSalePrices(): Flow<Map<String, Int>>

    /**
     * Mock 세일 적용: 주어진 상품들에 임의 할인율(기본 30%)을 적용해 세일가를 만든다.
     * "가격이 내려간" 상황을 시뮬레이션하기 위한 데모용 트리거다.
     *
     * @param originalPrices productId -> 원래 가격
     */
    suspend fun applyMockSale(originalPrices: Map<String, Int>)
}

@Singleton
class SaleRepositoryImpl @Inject constructor() : SaleRepository {

    private val salePrices = MutableStateFlow<Map<String, Int>>(emptyMap())

    override fun observeSalePrices(): Flow<Map<String, Int>> = salePrices.asStateFlow()

    override suspend fun applyMockSale(originalPrices: Map<String, Int>) {
        // 원가의 70%로 세일가 책정(30% 할인). 100원 단위로 내림.
        val discounted = originalPrices.mapValues { (_, price) ->
            ((price * (1.0 - DISCOUNT_RATE)).toInt() / 100) * 100
        }
        salePrices.update { it + discounted }
    }

    private companion object {
        const val DISCOUNT_RATE = 0.30
    }
}