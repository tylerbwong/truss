buildscript {
    val androidGradlePluginVersion: String by project
    val kotlinVersion: String by project
    val kspVersion: String by project

    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$androidGradlePluginVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
