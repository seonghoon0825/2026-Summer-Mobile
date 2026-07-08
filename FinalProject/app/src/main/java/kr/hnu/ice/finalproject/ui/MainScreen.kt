package kr.hnu.ice.finalproject.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.AuthUiState
import kr.hnu.ice.finalproject.MainViewModel
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.navigation.AppNavHost
import kr.hnu.ice.finalproject.navigation.Routes
import kr.hnu.ice.finalproject.navigation.TopLevelDestination

/**
 * 앱 메인 화면. 하단 탭바 + NavHost를 조립한다.
 * 하단 탭바는 최상위 목적지(홈/카테고리/검색/장바구니/마이)에서만 보이고,
 * 상품 상세 같은 하위 화면에서는 숨긴다.
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel(),
    // 가격 인하 알림 탭으로 전달된 productId(있으면 상세로 이동). 소비 후 onDeepLinkHandled 호출.
    deepLinkProductId: String? = null,
    onDeepLinkHandled: () -> Unit = {},
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()

    // 자동 로그인 판단이 끝날 때까지 로딩 표시.
    when (val state = authState) {
        AuthUiState.Loading -> LoadingIndicator()
        else -> {
            // 로그인 여부에 따라 시작 화면 결정(한 번만 결정되어 NavHost에 전달됨)
            val startDestination = if (state == AuthUiState.Authenticated) Routes.HOME else Routes.LOGIN

            // 로그인 상태일 때만 알림 딥링크로 상세 화면으로 이동한다.
            // (미로그인 시엔 소비하지 않고 남겨 두어, 로그인 후 이동되도록 한다)
            LaunchedEffect(deepLinkProductId, startDestination) {
                val productId = deepLinkProductId
                if (productId != null && startDestination == Routes.HOME) {
                    navController.navigate(Routes.productDetail(productId))
                    onDeepLinkHandled()
                }
            }

            MainContent(
                navController = navController,
                startDestination = startDestination,
                cartCount = cartCount,
            )
        }
    }
}

/** 하단 탭바 + NavHost 본문. */
@Composable
private fun MainContent(
    navController: NavHostController,
    startDestination: String,
    cartCount: Int,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // 현재 목적지가 최상위 탭 중 하나인지
    val isTopLevel = TopLevelDestination.entries.any { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true
    }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    cartCount = cartCount,
                    onTabSelected = { dest -> navController.navigateToTab(dest) },
                )
            }
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

/** 하단 탭바. */
@Composable
private fun AppBottomBar(
    currentDestination: androidx.navigation.NavDestination?,
    cartCount: Int,
    onTabSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { dest ->
            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(dest) },
                icon = {
                    // 장바구니 탭에는 담긴 개수 뱃지를 표시
                    if (dest == TopLevelDestination.CART && cartCount > 0) {
                        BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                            Icon(imageVector = dest.icon, contentDescription = dest.label)
                        }
                    } else {
                        Icon(imageVector = dest.icon, contentDescription = dest.label)
                    }
                },
                label = { Text(dest.label) },
            )
        }
    }
}

/**
 * 탭 이동 시 백스택을 깔끔하게 관리한다.
 * - 시작 목적지까지 popUpTo(saveState) 로 스택 누적 방지
 * - 같은 탭 재클릭 시 화면 중복 생성 방지(launchSingleTop)
 * - 이전 탭 상태 복원(restoreState)
 */
private fun NavHostController.navigateToTab(dest: TopLevelDestination) {
    navigate(dest.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}