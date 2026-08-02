plugins {
    id("ios26.library")
}

dependencies {
    implementation(project(":libs:config"))
    implementation(project(":hooks:hooks-api"))
}
