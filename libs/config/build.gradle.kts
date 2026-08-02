plugins {
    id("ios26.library")
}

dependencies {
    implementation(project(":libs:core"))
    implementation(project(":libs:schema"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(project(":libs:testing"))
}
