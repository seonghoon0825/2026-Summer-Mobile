package kr.hnu.ice.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme
import kr.hnu.ice.finalproject.ui.MainScreen

// 앱 진입점. DataStore의 다크모드 설정을 읽어 AppTheme에 반영하고 MainScreen을 띄운다.
// @AndroidEntryPoint: 화면에서 @HiltViewModel 주입을 받기 위한 Hilt 진입점.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // MainScreen과 동일한 인스턴스를 공유(같은 ViewModelStoreOwner=Activity)
            val viewModel: MainViewModel = hiltViewModel()
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
            AppTheme(darkTheme = darkTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}