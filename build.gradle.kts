buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.google.ksp.gradlePlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
