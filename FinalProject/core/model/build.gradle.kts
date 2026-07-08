plugins {
    alias(libs.plugins.android.library)
}

// :core:model
// 앱 전역에서 쓰는 순수 데이터 모델(도메인 모델)만 둔다.
// 어떤 모듈에도 의존하지 않는 최하위 계층이다.
android {
    namespace = "kr.hnu.ice.finalproject.core.model"
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
    // 의존성 없음: 최하위 계층
}