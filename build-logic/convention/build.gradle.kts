plugins {
    `kotlin-dsl`
}

group = "dev.ios26.buildlogic"

dependencies {
    // Convention plugins compile against the toolchain plugins they apply.
    implementation("com.android.tools.build:gradle:9.3.1")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.4.10")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.4.10")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
    implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:12.1.2")
}

gradlePlugin {
    plugins {
        create("ios26Library") {
            id = "ios26.library"
            implementationClass = "dev.ios26.buildlogic.Ios26LibraryConventionPlugin"
        }
        create("ios26Application") {
            id = "ios26.application"
            implementationClass = "dev.ios26.buildlogic.Ios26ApplicationConventionPlugin"
        }
        create("ios26Quality") {
            id = "ios26.quality"
            implementationClass = "dev.ios26.buildlogic.Ios26QualityConventionPlugin"
        }
        create("ios26Testing") {
            id = "ios26.testing"
            implementationClass = "dev.ios26.buildlogic.Ios26TestingConventionPlugin"
        }
        create("ios26Design") {
            id = "ios26.design"
            implementationClass = "dev.ios26.buildlogic.Ios26DesignConventionPlugin"
        }
        create("ios26ModuleInject") {
            id = "ios26.module-inject"
            implementationClass = "dev.ios26.buildlogic.Ios26ModuleInjectPlugin"
        }
        create("ios26Architecture") {
            id = "ios26.architecture"
            implementationClass = "dev.ios26.buildlogic.Ios26ArchitectureConventionPlugin"
        }
    }
}
