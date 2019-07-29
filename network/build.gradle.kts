plugins {
    kotlin("jvm")
    kotlin("kapt")
}

apply { from(rootProject.file("buildSrc/kotlin-sources.gradle")) }

dependencies {
    api(libs.rx.android)
    api(libs.kotlin.jdk8)

    implementation(libs.koin.core)
    api(libs.dagger.core)
    kapt(libs.dagger.annotation)

    implementation(libs.moshi.core)
    kapt(libs.moshi.codeGen)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter)
    implementation(libs.retrofit.adapter)
    implementation(libs.okHttpLogger)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockWebServer)
}