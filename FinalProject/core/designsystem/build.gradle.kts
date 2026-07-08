plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
}

// :core:designsystem
// 공용 Compose 컴포넌트/테마/색상/타이포를 둔다. feature 모듈들이 이 모듈의 UI를 재사용한다.
// (DI 대상이 없으므로 Hilt는 적용하지 않는다)
android {
    namespace = "kr.hnu.ice.finalproject.core.designsystem"
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

    buildFeatures {
        compose = true
    }
}

dependencies {
    // 공통 유틸(PriceFormatter 재사용). PriceText 내부에서만 쓰므로 implementation.
    implementation(project(":core:common"))

    // Compose/Material3는 이 모듈의 공개 UI API에 노출되므로 api로 전파한다.
    // (feature 모듈이 designsystem만 의존해도 Compose 타입을 함께 사용할 수 있게)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)

    // 이미지 로더: Coil3. NetworkImage에 노출되므로 coil-compose는 api,
    // 실제 네트워크 페처(okhttp)는 런타임 구현이라 implementation.
    api(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    debugImplementation(libs.androidx.compose.ui.tooling)
}