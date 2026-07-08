plugins {
    alias(libs.plugins.android.library)
}

// :core:common
// 특정 도메인에 얽매이지 않는 공통 유틸/확장함수/Result 래퍼/디스패처 등을 둔다.
// core:model 정도만 필요할 수 있으나, 지금은 독립적으로 둔다.
android {
    namespace = "kr.hnu.ice.finalproject.core.common"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // DispatcherProvider가 CoroutineDispatcher를 공개 API로 노출하므로 api로 전파한다.
    api(libs.kotlinx.coroutines.core)
}