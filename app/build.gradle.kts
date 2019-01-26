import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("kapt")
    kotlin("android")
    kotlin("android.extensions")
    id(projectPlugins.detekt).version(versions.detekt)
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))
val apiKey = localProperties["apiKey"] as String

java

android {
    compileSdkVersion(build.android.compileSdkVersion)
    buildToolsVersion(build.android.buildToolsVersion)

    defaultConfig {
        applicationId = "com.gumil.giphy"
        minSdkVersion(build.android.minSdkVersion)
        targetSdkVersion(build.android.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", apiKey)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }
}

dependencies {
    implementation(libs.kotlin.jdk8)

    implementation(libs.rx.android)

    implementation(libs.moshi.core)
    kapt(libs.moshi.codeGen)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter)
    implementation(libs.retrofit.adapter)
    implementation(libs.okHttpLogger)

    implementation(libs.android.appcompat)
    implementation(libs.android.ktx)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockWebServer)
    androidTestImplementation(libs.test.androidTestRunner)
    androidTestImplementation(libs.test.espresso)

    detektPlugins(libs.detektLint)
}

detekt {
    version = versions.detekt
    input = files("src/main/java", "src/androidx/java", "src/support/java")
    filters = ".*test.*,.*/resources/.*,.*/tmp/.*"
}