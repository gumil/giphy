object versions {
    const val kotlin = "1.3.20"
    const val detekt = "1.0.0-RC12"
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

    object test {
        const val junit = "junit:junit:4.12"
        const val espresso = "androidx.test.espresso:espresso-core:3.1.0"
        const val androidTestRunner = "androidx.test:runner:1.1.0"
    }

    const val detektLint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
}