package dev.ios26.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * `ios26.library` — Android library convention (AGP 9 built-in Kotlin, no kotlin-android plugin).
 * Applies: android-library + serialization + quality + testing. Namespace derived from module path.
 */
class Ios26LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("com.android.library")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
            plugins.apply("ios26.quality")
            plugins.apply("ios26.testing")

            extensions.configure<LibraryExtension> {
                namespace = "dev.ios26.${project.name.replace("-", "")}"
                compileSdk = Sdk.COMPILE
                defaultConfig {
                    minSdk = Sdk.MIN
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
            dependencies {
                add("implementation", libs(target, "kotlinx-coroutines-core"))
            }
        }
    }
}
