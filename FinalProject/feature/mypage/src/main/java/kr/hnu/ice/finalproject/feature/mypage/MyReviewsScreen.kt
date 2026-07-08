package kr.hnu.ice.finalproject.feature.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView

/**
 * 내 리뷰 화면. (Mock에는 작성 리뷰 데이터가 없어 빈 상태로 표시)
 */
@Composable
fun MyReviewsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        MyPageTopBar(title = "내 리뷰", onBack = onBack)
        EmptyView(message = "작성한 리뷰가 없어요.")
    }
}