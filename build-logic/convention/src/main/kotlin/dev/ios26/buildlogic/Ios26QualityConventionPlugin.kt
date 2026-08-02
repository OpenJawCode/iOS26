package dev.ios26.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * `ios26.quality` — ktlint + detekt. Static analysis is part of every module;
 * CI fails on violations (docs/ci.md).
 */
class Ios26QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jlleitschuh.gradle.ktlint")
            plugins.apply("io.gitlab.arturbosch.detekt")

            tasks.withType(Detekt::class.java).configureEach {
                // Module-local sources only (whole-repo scans per module were O(NxM) slow).
                setSource(projectDir)
                exclude("build/**", "**/build/**", "**/.gradle/**")
                ignoreFailures = false
            }
        }
    }
}
