plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    api(projects.trussRuntime)
    implementation(kotlin("stdlib"))
    implementation(libs.square.kotlinPoet)
    implementation(libs.google.autoService.annotations)
    ksp(libs.autoService.ksp)
    implementation(libs.google.ksp.symbolProcessingApi)
}
