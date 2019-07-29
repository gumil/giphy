@file:Suppress("unused", "ClassName")

object versions {
    const val kotlin = "1.3.41"
    const val detekt = "1.0.0-RC16"
    const val moshi = "1.8.0"
    const val retrofit = "2.6.0"
    const val okhttp = "4.0.1"
    const val koin = "2.0.1"
    const val kaskade = "0.2.3"
    const val livedata = "2.0.0"
    const val navigation = "2.1.0-beta02"
    const val rxBinding = "3.0.0-alpha2"
}

object build {
    const val androidGradle = "com.android.tools.build:gradle:3.4.2"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"

    object android {
        const val buildToolsVersion = "29.0.1"
        const val compileSdkVersion = 29
        const val minSdkVersion = 21
        const val targetSdkVersion = 29
    }
}

object projectPlugins {
    const val detekt = "io.gitlab.arturbosch.detekt"
}

object libs {
    object android {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0-rc01"
        const val ktx = "androidx.core:core-ktx:1.2.0-alpha01"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0-beta01"

        object navigation {
            const val fragment = "androidx.navigation:navigation-fragment-ktx:${versions.navigation}"
            const val ui = "androidx.navigation:navigation-ui-ktx:${versions.navigation}"
        }
        const val material = "com.google.android.material:material:1.1.0-alpha08"
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
        const val android = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val swipeRefreshLayout = "com.jakewharton.rxbinding3:rxbinding-swiperefreshlayout:${versions.rxBinding}"
        const val recyclerView = "com.jakewharton.rxbinding3:rxbinding-recyclerview:${versions.rxBinding}"
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
        const val livedata = "com.github.gumil.kaskade:kaskade-livedata:${versions.kaskade}"
    }

    const val glide = "com.github.bumptech.glide:glide:4.9.0"

    object test {
        const val junit = "junit:junit:4.12"

        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${versions.okhttp}"
        const val mockK = "io.mockk:mockk:1.9.3"

        const val livedata = "androidx.arch.core:core-testing:${versions.livedata}"

        const val espresso = "androidx.test.espresso:espresso-core:3.1.0"
        const val androidTestRunner = "androidx.test:runner:1.1.0"
    }

    const val detektLint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
}