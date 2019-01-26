plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id(projectPlugins.detekt).version(versions.detekt)
}

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
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }
}

dependencies {
    implementation(project(":network"))
    implementation(libs.koin.android)
    implementation(libs.koin.viewModel)

    implementation(libs.android.appcompat)
    implementation(libs.android.ktx)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidTestRunner)
    androidTestImplementation(libs.test.espresso)

    detektPlugins(libs.detektLint)
}

detekt {
    version = versions.detekt
    input = files("src/main/java", "src/androidx/java", "src/support/java")
    filters = ".*test.*,.*/resources/.*,.*/tmp/.*"
}