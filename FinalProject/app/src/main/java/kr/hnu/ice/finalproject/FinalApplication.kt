package kr.hnu.ice.finalproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Hilt DI 컨테이너의 진입점.
// @HiltAndroidApp 이 붙으면 앱 전역 의존성 그래프가 생성된다.
@HiltAndroidApp
class FinalApplication : Application()