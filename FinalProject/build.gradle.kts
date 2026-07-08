// 최상위 빌드 스크립트.
// 여기서는 프로젝트 전체에서 쓸 플러그인을 alias로 "선언"만 하고(apply false),
// 실제 적용은 각 모듈(app, feature, core...)의 build.gradle.kts에서 한다.
// AGP 9.2.1은 Kotlin(2.2.10)을 빌트인으로 적용하므로 kotlin-android 플러그인을 따로 선언하지 않는다.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false

    // 후속 STEP(멀티모듈)에서 사용할 플러그인들. 지금은 선언만 해두고 사용하지 않는다.
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}