plugins {
    id("ios26.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "dev.ios26.controlcenter"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":libs:design"))
    implementation(project(":libs:config"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.common)
    implementation(libs.androidx.savedstate)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation)
}
