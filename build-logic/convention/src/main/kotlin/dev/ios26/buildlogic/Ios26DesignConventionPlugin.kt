package dev.ios26.buildlogic

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

/**
 * `ios26.design` — design-system module convention (D-P2.3: pure foundation, no material3).
 * Applies: ios26.library + Compose compiler plugin + buildFeatures.compose + the
 * token-generation pipeline (tokens.json -> generated Tokens.kt, D-P2.4).
 */
class Ios26DesignConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("ios26.library")
            plugins.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            val tokensDir = layout.projectDirectory.dir("tokens")
            val generatedDir = layout.buildDirectory.dir("generated/designTokens/kotlin")

            val generateTask = tasks.register("generateDesignTokens", DesignTokensTask::class.java) {
                group = "design"
                description = "Generates Tokens.kt from tokens.json (source of truth)."
                tokensFile.set(tokensDir.file("tokens.json"))
                outputDir.set(generatedDir)
            }

            val androidComponents = extensions.getByType<LibraryAndroidComponentsExtension>()
            // Generated tokens are a source of truth dependency: compile against them.
            androidComponents.onVariants(androidComponents.selector().all()) { variant ->
                variant.sources.kotlin?.addGeneratedSourceDirectory(
                    generateTask,
                    { task: DesignTokensTask -> task.outputDir },
                )
            }
        }
    }
}
