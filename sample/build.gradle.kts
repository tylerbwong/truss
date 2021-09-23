plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
}

android {
    defaultConfig {
        applicationId = "me.tylerbwong.truss.sample"
        compileSdk = 31
        minSdk = 23
    }

    buildFeatures {
        buildConfig = false
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(projects.trussProcessor)
    implementation(projects.trussRuntime)
    ksp(projects.trussProcessor)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.google.material)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinCompileTesting.ksp)
    testImplementation(libs.google.truth)
}
