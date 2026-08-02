package dev.ios26.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * `ios26.module-inject` — registers InjectModuleMetadataTask for LSPosed module APKs
 * (ADR-0032): injects META-INF/xposed module metadata into every assemble output.
 */
class Ios26ModuleInjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val inject = target.tasks.register("injectModuleMetadata", InjectModuleMetadataTask::class.java) {
            group = "build"
            metadataFiles.from(
                target.layout.projectDirectory.file("src/main/resources/META-INF/xposed/java_init.list"),
                target.layout.projectDirectory.file("src/main/resources/META-INF/xposed/module.prop"),
                target.layout.projectDirectory.file("src/main/resources/META-INF/xposed/scope.list"),
            )
            apkFile.set(target.layout.buildDirectory.file("outputs/apk/debug/${target.name}-debug.apk"))
        }
        target.tasks.matching { it.name.contains("assemble") }.configureEach {
            finalizedBy(inject)
        }
    }
}
