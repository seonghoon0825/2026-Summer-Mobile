package kr.hnu.ice.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme
import kr.hnu.ice.finalproject.ui.MainScreen

// 앱 진입점. DataStore의 다크모드 설정을 읽어 AppTheme에 반영하고 MainScreen을 띄운다.
// @AndroidEntryPoint: 화면에서 @HiltViewModel 주입을 받기 위한 Hilt 진입점.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 가격 인하 알림 탭으로 전달된 productId. null이 아니면 MainScreen이 상세로 이동한다.
    private var deepLinkProductId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkProductId = intent?.getStringExtra(EXTRA_PRODUCT_ID)
        setContent {
            // MainScreen과 동일한 인스턴스를 공유(같은 ViewModelStoreOwner=Activity)
            val viewModel: MainViewModel = hiltViewModel()
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
            val fontScale by viewModel.fontScale.collectAsStateWithLifecycle()
            val highContrast by viewModel.highContrast.collectAsStateWithLifecycle()
            AppTheme(darkTheme = darkTheme, fontScale = fontScale, highContrast = highContrast) {
                MainScreen(
                    viewModel = viewModel,
                    deepLinkProductId = deepLinkProductId,
                    onDeepLinkHandled = { deepLinkProductId = null },
                )
            }
        }
    }

    // 앱이 이미 실행 중일 때 알림을 탭하면 여기로 들어온다(launchMode=singleTop).
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkProductId = intent.getStringExtra(EXTRA_PRODUCT_ID)
    }

    companion object {
        /** 알림 PendingIntent가 담는 상품 id extra 키. */
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}