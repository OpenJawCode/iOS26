package dev.ios26.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskAction

/**
 * Enforces ARCHITECTURE.md §3.1: allowed dependency edges per module, no dependencies on
 * unregistered modules, and a README.md ownership anchor per module. CI fails on violations.
 */
abstract class ArchitectureValidateTask : DefaultTask() {

    @TaskAction
    fun validate() {
        val root = project.rootProject
        val edges: Map<String, Set<String>> = mapOf(
            ":libs:core" to emptySet(),
            ":libs:schema" to emptySet(),
            ":libs:testing" to emptySet(),
            ":libs:config" to setOf(":libs:core", ":libs:schema"),
            ":launcher:app" to emptySet(),
            ":benchmarks:macrobenchmark" to emptySet(),
            ":launcher:baseline-prof" to emptySet(),
        )
        val known = edges.keys
        val errors = mutableListOf<String>()

        root.subprojects.forEach { module ->
            val deps = module.configurations
                .filter { it.name in ALLOWED_CONFIGURATIONS }
                .flatMap { it.dependencies }
                .filterIsInstance<ProjectDependency>()
                .map { it.path }
                .toSet()

            val illegal = deps - edges.getOrDefault(module.path, emptySet())
            if (illegal.isNotEmpty()) {
                errors += "${module.path} depends on ${illegal.sorted()}, not allowed (ARCHITECTURE.md §3.1)"
            }
            val unknown = deps - known
            if (unknown.isNotEmpty()) {
                errors += "${module.path} depends on unregistered module(s) ${unknown.sorted()}"
            }
            if (!module.file("README.md").exists()) {
                errors += "${module.path} is missing README.md (ownership anchor, AGENTS.md §2)"
            }
        }

        check(errors.isEmpty()) {
            "Architecture violations:\n" + errors.joinToString("\n") { "  - $it" }
        }
    }

    private companion object {
        val ALLOWED_CONFIGURATIONS = setOf("api", "implementation", "compileOnly")
    }
}
