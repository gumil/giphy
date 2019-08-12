plugins {
    kotlin("jvm")
    kotlin("kapt")
}

apply { from(rootProject.file("buildSrc/kotlin-sources.gradle")) }

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.jdk8)

    implementation(libs.koin.core)

    implementation(libs.moshi.core)
    kapt(libs.moshi.codeGen)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter)
    implementation(libs.okHttpLogger)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockWebServer)
}