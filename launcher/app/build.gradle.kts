plugins {
    id("ios26.application")
}

android {
    namespace = "dev.ios26.launcher"
    defaultConfig {
        applicationId = "dev.ios26.launcher"
    }
}

dependencies {
    implementation(project(":libs:design"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.compose)
}
