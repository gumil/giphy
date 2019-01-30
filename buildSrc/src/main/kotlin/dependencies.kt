@file:Suppress("unused", "ClassName")

object versions {
    const val kotlin = "1.3.20"
    const val detekt = "1.0.0-RC12"
    const val moshi = "1.8.0"
    const val retrofit = "2.5.0"
    const val okhttp = "3.12.0"
    const val koin = "2.0.0-beta-1"
    const val kaskade = "0.2.1"
}

object build {
    const val androidGradle = "com.android.tools.build:gradle:3.3.0"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"

    object android {
        const val buildToolsVersion = "28.0.3"
        const val compileSdkVersion = 28
        const val minSdkVersion = 21
        const val targetSdkVersion = 28
    }
}

object projectPlugins {
    const val detekt = "io.gitlab.arturbosch.detekt"
}

object libs {
    object android {
        const val appcompat = "androidx.appcompat:appcompat:1.0.0"
        const val ktx = "androidx.core:core-ktx:1.0.0"
    }

    object kotlin {
        const val core = "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}"
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"
    }

    object moshi {
        const val core = "com.squareup.moshi:moshi:${versions.moshi}"
        const val codeGen = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
    }

    object retrofit {
        const val core = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
        const val converter = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
        const val adapter = "com.squareup.retrofit2:adapter-rxjava2:${versions.retrofit}"
    }

    const val okHttpLogger = "com.squareup.okhttp3:logging-interceptor:${versions.okhttp}"

    object rx {
        const val android = "io.reactivex.rxjava2:rxandroid:2.1.0"
    }

    object koin {
        const val core = "org.koin:koin-core:${versions.koin}"
        const val android = "org.koin:koin-android:${versions.koin}"
        const val scope = "org.koin:koin-android-scope:${versions.koin}"
        const val viewModel = "org.koin:koin-android-viewmodel:${versions.koin}"
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:1.6.3"

    object kaskade {
        const val core = "com.github.gumil.kaskade:kaskade:${versions.kaskade}"
        const val rx = "com.github.gumil.kaskade:kaskade-rx:${versions.kaskade}"
    }

    object test {
        const val junit = "junit:junit:4.12"

        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${versions.okhttp}"

        const val espresso = "androidx.test.espresso:espresso-core:3.1.0"
        const val androidTestRunner = "androidx.test:runner:1.1.0"
    }

    const val detektLint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
}