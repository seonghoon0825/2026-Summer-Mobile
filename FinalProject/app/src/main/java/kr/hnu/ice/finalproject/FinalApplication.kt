package kr.hnu.ice.finalproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kr.hnu.ice.finalproject.notification.PriceAlertNotifier
import javax.inject.Inject

// Hilt DI 컨테이너의 진입점.
// @HiltAndroidApp 이 붙으면 앱 전역 의존성 그래프가 생성된다.
@HiltAndroidApp
class FinalApplication : Application() {

    // 앱 시작 시 주입되며, 생성 시점(init)에 가격 인하 알림 채널을 등록한다.
    @Inject
    lateinit var priceAlertNotifier: PriceAlertNotifier
}