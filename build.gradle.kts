// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(build.androidGradle)
        classpath(build.kotlinGradle)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
