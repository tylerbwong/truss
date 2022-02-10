buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.google.ksp.gradlePlugin)
        classpath(libs.mavenPublish.gradlePlugin)
        classpath(libs.metalava.gradlePlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
