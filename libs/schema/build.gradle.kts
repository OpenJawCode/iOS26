plugins {
    id("ios26.library")
}

android {
    sourceSets {
        getByName("main") {
            resources.srcDirs("src/main/schemas")
        }
    }
}

dependencies {
    implementation(libs.networknt)
}
