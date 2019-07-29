import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id(projectPlugins.detekt).version(versions.detekt)
}

apply { from(rootProject.file("buildSrc/kotlin-sources.gradle")) }

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))
val apiKey = localProperties["apiKey"] as String

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

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(project(":network"))

    implementation(libs.kaskade.core)
    implementation(libs.kaskade.rx)
    implementation(libs.kaskade.livedata)

    implementation(libs.koin.android)
    implementation(libs.koin.viewModel)

    implementation(libs.timber)
    implementation(libs.leakCanary)

    implementation(libs.glide)

    implementation(libs.rx.swipeRefreshLayout)
    implementation(libs.rx.recyclerView)

    implementation(libs.android.appcompat)
    implementation(libs.android.recyclerView)
    implementation(libs.android.navigation.fragment)
    implementation(libs.android.navigation.ui)
    implementation(libs.android.material)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockK)
    testImplementation(libs.test.livedata)
    androidTestImplementation(libs.test.androidTestRunner)
    androidTestImplementation(libs.test.espresso)

    detektPlugins(libs.detektLint)
}

detekt {
    version = versions.detekt
    input = files("src/main/java", "src/androidx/java", "src/support/java")
}