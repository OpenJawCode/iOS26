package dev.ios26.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/** `ios26.testing` — Tier-1 test dependencies (docs/testing.md). */
class Ios26TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("testImplementation", libs(target, "junit4"))
                add("testImplementation", libs(target, "kotlinx-coroutines-test"))
            }
        }
    }
}

fun libs(project: Project, name: String): Any {
    val catalog: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    return catalog.findLibrary(name).get().get()
}
