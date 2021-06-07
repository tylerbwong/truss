plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(projects.trussRuntime)
    implementation(kotlin("stdlib"))
    implementation(libs.square.kotlinPoet)
    implementation(libs.google.autoService.annotations)
    ksp(libs.autoService.ksp)
    compileOnly(libs.google.ksp.symbolProcessingApi)
}
