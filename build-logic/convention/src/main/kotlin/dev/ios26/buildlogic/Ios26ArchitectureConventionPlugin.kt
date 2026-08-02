package dev.ios26.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty

/**
 * `ios26.architecture` — root-project convention enforcing ARCHITECTURE.md §3.1:
 * module dependency rules, README anchors, MODULES.md generation. CI fails on violations.
 */
class Ios26ArchitectureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        check(target == target.rootProject) { "ios26.architecture applies to the root project only" }

        target.tasks.register("architectureValidate", ArchitectureValidateTask::class.java) {
            group = "verification"
            description = "Enforces ARCHITECTURE.md §3.1 dependency rules + module README presence."
        }

        target.tasks.register("generateModulesDoc", GenerateModulesDocTask::class.java) {
            group = "documentation"
            description = "Generates MODULES.md from the module graph (module, purpose, dependencies)."
            rows.set(moduleDocsFor(target))
            outputFile.set(target.rootProject.file("MODULES.md"))
        }
    }
}
