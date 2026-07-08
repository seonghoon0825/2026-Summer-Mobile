plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // assets JSON(Mock) 파싱용 kotlinx.serialization
    alias(libs.plugins.kotlin.serialization)
}

// :core:data
// Repository 구현과 데이터 출처(Mock/Room/DataStore)를 숨긴다.
// feature 모듈은 이 모듈의 Repository 인터페이스만 알고, 데이터 출처는 모른다.
// 의존: core:model, core:common (아래로만 흐른다)
android {
    namespace = "kr.hnu.ice.finalproject.core.data"
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

// Room 스키마 출력 위치 (마이그레이션 검증용)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
}

dependencies {
    // 도메인 모델은 Repository 공개 API(반환/파라미터 타입)에 노출되므로 api로 전파한다.
    // (feature 모듈이 core:data만 의존해도 Product/User 등 도메인 모델을 쓸 수 있게)
    api(project(":core:model"))
    implementation(project(":core:common"))

    // 비동기
    implementation(libs.kotlinx.coroutines.core)

    // JSON 파싱 (Mock assets)
    implementation(libs.kotlinx.serialization.json)

    // 로컬 저장: Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // 로컬 저장: DataStore
    implementation(libs.androidx.datastore.preferences)

    // DI: Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}