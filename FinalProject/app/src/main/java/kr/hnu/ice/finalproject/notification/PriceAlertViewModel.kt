package kr.hnu.ice.finalproject.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.SaleRepository
import kr.hnu.ice.finalproject.core.data.repository.WishRepository
import javax.inject.Inject

/**
 * 찜 상품 가격 인하 알림 로직.
 * 1) 찜 목록을 읽어 2) Mock 세일을 적용하고 3) 가격이 내려간 상품을 골라 4) 로컬 알림을 발송한다.
 */
@HiltViewModel
class PriceAlertViewModel @Inject constructor(
    private val wishRepository: WishRepository,
    private val saleRepository: SaleRepository,
    private val notifier: PriceAlertNotifier,
) : ViewModel() {

    // 화면에 결과를 알려주는 일회성 메시지(스낵바/토스트용)
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    /** 찜 상품에 Mock 세일을 적용하고, 가격이 내려간 상품이 있으면 알림을 발송한다. */
    fun checkAndNotifyWishlistSale() {
        viewModelScope.launch {
            val wishItems = wishRepository.getWishItems().first()
            if (wishItems.isEmpty()) {
                _messages.emit("찜한 상품이 없어요. 상품을 먼저 찜해 보세요.")
                return@launch
            }

            // Mock: 찜한 상품들에 임의 세일가 적용(가격 인하 시뮬레이션)
            val originalPrices = wishItems.associate { it.product.id to it.product.price }
            saleRepository.applyMockSale(originalPrices)

            val sales = saleRepository.observeSalePrices().first()
            val drops = wishItems.mapNotNull { item ->
                val salePrice = sales[item.product.id] ?: return@mapNotNull null
                if (salePrice < item.product.price) {
                    PriceDrop(
                        brand = item.product.brand,
                        name = item.product.name,
                        oldPrice = item.product.price,
                        newPrice = salePrice,
                    )
                } else {
                    null
                }
            }

            if (drops.isEmpty()) {
                _messages.emit("가격이 내려간 찜 상품이 없어요.")
            } else {
                notifier.notifyPriceDrops(drops)
                _messages.emit("${drops.size}개 상품의 가격 인하 알림을 보냈어요.")
            }
        }
    }
}