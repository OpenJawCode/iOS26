package dev.ios26.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure

/**
 * `ios26.application` — Android application convention. Namespace derived from module path;
 * applicationId is explicit per module (intentional, ADR-0004 release train).
 */
class Ios26ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("com.android.application")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
            plugins.apply("ios26.quality")
            plugins.apply("ios26.testing")

            extensions.configure<ApplicationExtension> {
                namespace = "dev.ios26.${project.name.replace("-", "")}"
                compileSdk = Sdk.COMPILE
                defaultConfig {
                    minSdk = Sdk.MIN
                    targetSdk = Sdk.TARGET
                    versionCode = 1
                    versionName = "0.1.0"
                }
                buildTypes {
                    release {
                        isMinifyEnabled = false
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }
}
