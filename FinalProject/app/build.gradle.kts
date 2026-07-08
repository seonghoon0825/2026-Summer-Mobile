plugins {
    alias(libs.plugins.android.application)
    // Compose 컴파일러 플러그인. (AGP 9의 빌트인 Kotlin 위에 Compose 컴파일러를 얹는다)
    alias(libs.plugins.compose.compiler)
    // DI: Hilt (@HiltAndroidApp Application이 여기 있으므로 app에도 적용)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "kr.hnu.ice.finalproject"
    // core-ktx 1.19.0 등 최신 AndroidX가 compileSdk 37을 요구한다.
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "kr.hnu.ice.finalproject"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Compose 사용 선언
    buildFeatures {
        compose = true
    }
}

fun ksp(compiler: Provider<MinimalExternalModuleDependency>) {}

dependencies {
    // ---- feature 모듈 (app이 모든 화면을 조립하고 네비게이션을 호스팅) ----
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:category"))
    implementation(project(":feature:search"))
    implementation(project(":feature:productdetail"))
    implementation(project(":feature:cart"))
    implementation(project(":feature:order"))
    implementation(project(":feature:mypage"))

    // ---- core 모듈 (app에서 직접 쓰는 것: 테마, 공통, 자동로그인 상태) ----
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    // ---- DI: Hilt ----
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ---- Navigation (NavHost는 app이 소유) ----
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // AndroidX 기본 + 수명주기
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM: 이 BOM이 아래 compose 라이브러리들의 버전을 통일한다.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Debug 전용: Preview / Compose UI 검사 도구
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}