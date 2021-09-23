plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(projects.trussRuntime)
    implementation(libs.square.kotlinPoet)
    implementation(libs.google.autoService.annotations)
    ksp(libs.autoService.ksp)
    compileOnly(libs.google.ksp.symbolProcessingApi)
}
