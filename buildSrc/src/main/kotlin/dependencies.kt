@file:Suppress("unused", "ClassName")

object versions {
    const val kotlin = "1.3.72"
    const val detekt = "1.10.0-RC1"
    const val moshi = "1.9.3"
    const val retrofit = "2.9.0"
    const val okhttp = "4.7.2"
    const val dagger = "2.28"
    const val kaskade = "0.3.8"
    const val livedata = "2.0.0"
    const val navigation = "2.2.2"
    const val coroutines = "1.3.7"
}

object build {
    const val androidGradle = "com.android.tools.build:gradle:4.0.0"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${versions.dagger}-alpha"

    object android {
        const val buildToolsVersion = "30.0.0"
        const val compileSdkVersion = 29
        const val minSdkVersion = 23
        const val targetSdkVersion = 29
    }
}

object projectPlugins {
    const val detekt = "io.gitlab.arturbosch.detekt"
}

object libs {
    object android {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val ktx = "androidx.core:core-ktx:1.3.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"

        object navigation {
            const val fragment = "androidx.navigation:navigation-fragment-ktx:${versions.navigation}"
            const val ui = "androidx.navigation:navigation-ui-ktx:${versions.navigation}"
        }
        const val material = "com.google.android.material:material:1.3.0-alpha01"
    }

    object kotlin {
        const val core = "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}"
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
    }

    object moshi {
        const val core = "com.squareup.moshi:moshi:${versions.moshi}"
        const val codeGen = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
    }

    object retrofit {
        const val core = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
        const val converter = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
    }

    const val okHttpLogger = "com.squareup.okhttp3:logging-interceptor:${versions.okhttp}"

    object dagger {
        const val core = "com.google.dagger:dagger:${versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${versions.dagger}"

        object hilt {
            const val core = "com.google.dagger:hilt-android:${versions.dagger}-alpha"
            const val compiler = "com.google.dagger:hilt-android-compiler:${versions.dagger}-alpha"
            const val viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01"
            const val androidCompiler = "androidx.hilt:hilt-compiler:1.0.0-alpha01"
        }
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.4"

    object kaskade {
        const val core = "dev.gumil.kaskade:core:${versions.kaskade}"
        const val coroutines = "dev.gumil.kaskade:coroutines:${versions.kaskade}"
        const val livedata = "dev.gumil.kaskade:livedata:${versions.kaskade}"
    }

    const val coil = "io.coil-kt:coil:0.11.0"
    const val coilGif = "io.coil-kt:coil-gif:0.11.0"

    object test {
        const val junit = "junit:junit:4.12"

        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${versions.okhttp}"
        const val mockK = "io.mockk:mockk:1.9.3"

        const val livedata = "androidx.arch.core:core-testing:${versions.livedata}"

        const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
        const val androidTestRunner = "androidx.test:runner:1.2.0"

        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${versions.coroutines}"
    }

    const val detektLint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
}
