package kr.hnu.ice.finalproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 앱 전역 route 정의. feature 간 이동은 여기(app)에서 관리한다.
 * (feature 모듈은 서로를 모르고, 콜백으로만 이동 의사를 알린다)
 */
object Routes {
    // 인증 (하단 탭 아님)
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    const val HOME = "home"
    const val CATEGORY = "category"
    const val SEARCH = "search"

    // 선택한 카테고리의 상품 목록
    const val CATEGORY_PRODUCTS = "category/{categoryId}"
    const val ARG_CATEGORY_ID = "categoryId"
    const val CART = "cart"
    const val MYPAGE = "mypage"

    // 마이페이지 하위 화면 (하단 탭 아님)
    const val ORDER_HISTORY = "order_history"
    const val WISHLIST = "wishlist"
    const val SETTINGS = "settings"
    const val MY_REVIEWS = "my_reviews"

    // 주문 흐름 (중첩 그래프: 주문서 → 결제 → 완료. 세 화면이 OrderViewModel을 공유)
    const val ORDER_GRAPH = "order_graph"
    const val ORDER = "order"
    const val PAYMENT = "payment"
    const val ORDER_COMPLETE = "order_complete"

    // 상품 상세: productId를 path 인자로 받는다.
    const val PRODUCT_DETAIL = "product/{productId}"
    const val ARG_PRODUCT_ID = "productId"

    // 상품 비교 (비교함 기반)
    const val COMPARE = "compare"

    /** 상품 상세로 이동할 실제 route 문자열을 만든다. */
    fun productDetail(productId: String): String = "product/$productId"

    /** 카테고리 상품 목록으로 이동할 실제 route 문자열을 만든다. */
    fun categoryProducts(categoryId: String): String = "category/$categoryId"
}

/**
 * 하단 탭바에 노출되는 최상위 목적지 5개.
 *
 * @param route NavHost route
 * @param label 탭 라벨
 * @param icon 탭 아이콘
 */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(Routes.HOME, "홈", Icons.Filled.Home),
    CATEGORY(Routes.CATEGORY, "카테고리", Icons.Filled.Category),
    SEARCH(Routes.SEARCH, "검색", Icons.Filled.Search),
    CART(Routes.CART, "장바구니", Icons.Filled.ShoppingCart),
    MYPAGE(Routes.MYPAGE, "마이", Icons.Filled.Person),
}