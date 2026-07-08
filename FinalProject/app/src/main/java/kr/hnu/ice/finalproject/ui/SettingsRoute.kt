package kr.hnu.ice.finalproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kr.hnu.ice.finalproject.feature.mypage.SettingsScreen
import kr.hnu.ice.finalproject.notification.PriceAlertViewModel

/**
 * 설정 화면 + 찜 가격 인하 알림의 플랫폼 처리(알림 권한 요청, 발송).
 * 알림/권한은 app이 담당하고, feature:mypage의 SettingsScreen은 콜백만 노출한다.
 */
@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: PriceAlertViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // ViewModel 메시지 → Toast
    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // POST_NOTIFICATIONS 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.checkAndNotifyWishlistSale()
        } else {
            Toast.makeText(context, "알림 권한이 거부되어 알림을 보낼 수 없어요.", Toast.LENGTH_SHORT).show()
        }
    }

    SettingsScreen(
        onBack = onBack,
        onLoggedOut = onLoggedOut,
        onSendPriceAlert = {
            // Android 13+는 런타임 알림 권한이 필요하다.
            val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED

            if (needsPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.checkAndNotifyWishlistSale()
            }
        },
    )
}