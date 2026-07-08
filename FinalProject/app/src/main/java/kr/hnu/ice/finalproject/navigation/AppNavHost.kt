package kr.hnu.ice.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import kr.hnu.ice.finalproject.feature.auth.LoginScreen
import kr.hnu.ice.finalproject.feature.auth.SignUpScreen
import kr.hnu.ice.finalproject.feature.cart.CartScreen
import kr.hnu.ice.finalproject.feature.category.CategoryProductListScreen
import kr.hnu.ice.finalproject.feature.category.CategoryScreen
import kr.hnu.ice.finalproject.feature.home.HomeScreen
import kr.hnu.ice.finalproject.feature.mypage.MyPageScreen
import kr.hnu.ice.finalproject.feature.mypage.MyReviewsScreen
import kr.hnu.ice.finalproject.feature.mypage.OrderHistoryScreen
import kr.hnu.ice.finalproject.feature.mypage.WishlistScreen
import kr.hnu.ice.finalproject.ui.SettingsRoute
import kr.hnu.ice.finalproject.feature.order.OrderCompleteScreen
import kr.hnu.ice.finalproject.feature.order.OrderScreen
import kr.hnu.ice.finalproject.feature.order.OrderViewModel
import kr.hnu.ice.finalproject.feature.order.PaymentScreen
import kr.hnu.ice.finalproject.feature.productdetail.ComparisonScreen
import kr.hnu.ice.finalproject.feature.productdetail.ProductDetailScreen
import kr.hnu.ice.finalproject.feature.search.SearchScreen

/**
 * 앱 전역 NavHost. 각 route를 해당 feature 화면에 연결한다.
 * feature 간 이동(홈 → 상품 상세 등)은 여기서 route로 처리한다.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // ---- 인증 ----
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigateToHomeAfterAuth() },
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) },
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigateToHomeAfterAuth() },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
            )
        }
        composable(Routes.CATEGORY) {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.categoryProducts(categoryId))
                },
            )
        }
        composable(
            route = Routes.CATEGORY_PRODUCTS,
            arguments = listOf(navArgument(Routes.ARG_CATEGORY_ID) { type = NavType.StringType }),
        ) {
            CategoryProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
            )
        }
        composable(Routes.CART) {
            CartScreen(
                onOrderClick = { navController.navigate(Routes.ORDER_GRAPH) },
            )
        }
        composable(Routes.MYPAGE) {
            MyPageScreen(
                onNavigateToOrders = { navController.navigate(Routes.ORDER_HISTORY) },
                onNavigateToWishlist = { navController.navigate(Routes.WISHLIST) },
                onNavigateToReviews = { navController.navigate(Routes.MY_REVIEWS) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ORDER_HISTORY) {
            OrderHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.WISHLIST) {
            WishlistScreen(
                onProductClick = { productId -> navController.navigate(Routes.productDetail(productId)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.MY_REVIEWS) {
            MyReviewsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsRoute(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    // 로그아웃 → 로그인 화면으로, 백스택 전체 제거
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }

        // ---- 주문 흐름(중첩 그래프): 주문서 → 결제 → 완료. 세 화면이 OrderViewModel을 공유 ----
        navigation(startDestination = Routes.ORDER, route = Routes.ORDER_GRAPH) {
            composable(Routes.ORDER) { entry ->
                val vm = navController.orderGraphViewModel(entry)
                OrderScreen(
                    viewModel = vm,
                    onProceedToPayment = { navController.navigate(Routes.PAYMENT) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.PAYMENT) { entry ->
                val vm = navController.orderGraphViewModel(entry)
                PaymentScreen(
                    viewModel = vm,
                    onPaymentSuccess = {
                        navController.navigate(Routes.ORDER_COMPLETE) {
                            // 주문서/결제 화면 제거(뒤로가기로 되돌아오지 못하게)
                            popUpTo(Routes.ORDER) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.ORDER_COMPLETE) { entry ->
                val vm = navController.orderGraphViewModel(entry)
                OrderCompleteScreen(
                    viewModel = vm,
                    onGoHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ORDER_GRAPH) { inclusive = true }
                        }
                    },
                    onGoOrders = {
                        navController.navigate(Routes.MYPAGE) {
                            popUpTo(Routes.ORDER_GRAPH) { inclusive = true }
                        }
                    },
                )
            }
        }

        // 최상위 탭이 아닌 상세 화면(스택 위로 push)
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PRODUCT_ID) { type = NavType.StringType }),
        ) {
            // productId는 ProductDetailViewModel이 SavedStateHandle로 직접 읽는다.
            ProductDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCompare = { navController.navigate(Routes.COMPARE) },
            )
        }

        composable(Routes.COMPARE) {
            ComparisonScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
                onBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * 인증 성공 후 홈으로 이동. 로그인/회원가입 화면을 백스택에서 제거해
 * 뒤로가기로 인증 화면에 돌아오지 못하게 한다.
 */
private fun NavHostController.navigateToHomeAfterAuth() {
    navigate(Routes.HOME) {
        popUpTo(Routes.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * 주문 흐름 세 화면이 같은 OrderViewModel을 공유하도록,
 * ORDER_GRAPH 백스택 엔트리에 스코프된 ViewModel을 반환한다.
 */
@Composable
private fun NavHostController.orderGraphViewModel(entry: NavBackStackEntry): OrderViewModel {
    val parentEntry = remember(entry) { getBackStackEntry(Routes.ORDER_GRAPH) }
    return hiltViewModel(parentEntry)
}