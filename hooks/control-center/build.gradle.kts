
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("ios26.quality")
    id("ios26.testing")
    id("ios26.module-inject")
}

android {
    namespace = "dev.ios26.hooks.controlcenter"
    compileSdk = 36
    defaultConfig {
        applicationId = "dev.ios26.hooks.controlcenter"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":hooks:hooks-common"))
    implementation(project(":hooks:hooks-api"))
    implementation(project(":libs:config"))
    compileOnly(project(":hooks:libxposed-api"))
}

// Modern module metadata (ADR-0032): injected by the ios26.module-inject plugin (META-INF/xposed/*).
