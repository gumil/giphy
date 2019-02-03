import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id(projectPlugins.detekt).version(versions.detekt)
}

apply { from(rootProject.file("buildSrc/kotlin-sources.gradle")) }

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
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }
}

androidExtensions {
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

dependencies {
    implementation(project(":network"))

    implementation(libs.kaskade.rx)
    implementation(libs.kaskade.livedata)

    implementation(libs.koin.android)
    implementation(libs.koin.viewModel)

    implementation(libs.timber)
    implementation(libs.leakCanary)

    implementation(libs.android.appcompat)
    implementation(libs.android.recyclerView)
    implementation(libs.android.ktx)

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
    filters = ".*test.*,.*/resources/.*,.*/tmp/.*"
}