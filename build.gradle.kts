buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.ksp.gradle)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
