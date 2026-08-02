pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "iOS26"

includeBuild("build-logic")

include(":libs:core")
include(":libs:schema")
include(":libs:config")
include(":libs:testing")
include(":launcher:app")
include(":benchmarks:macrobenchmark")
include(":launcher:baseline-prof")
