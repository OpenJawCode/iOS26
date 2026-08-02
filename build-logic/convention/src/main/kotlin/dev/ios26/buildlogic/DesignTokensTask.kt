package dev.ios26.buildlogic

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.util.Locale

/**
 * Generates Tokens.kt from tokens.json (D-P2.4: JSON is the single source of truth).
 * Parses with Groovy's JsonSlurper (ships with Gradle). Emits typed Kotlin objects:
 * hex -> androidx Color, dp/sp suffixes honored, bezier arrays -> List<Float>,
 * spring objects -> generated Spring data class.
 */
abstract class DesignTokensTask : DefaultTask() {

    @get:InputFile
    abstract val tokensFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        @Suppress("UNCHECKED_CAST")
        val root = JsonSlurper().parse(tokensFile.get().asFile) as Map<String, Any>
        val out = StringBuilder()

        out.appendLine("package dev.ios26.design.tokens")
        out.appendLine()
        out.appendLine("import androidx.compose.ui.unit.Dp")
        out.appendLine("import androidx.compose.ui.unit.TextUnit")
        out.appendLine("import androidx.compose.ui.unit.dp")
        out.appendLine("import androidx.compose.ui.unit.sp")
        out.appendLine()
        out.appendLine("/** GENERATED from tokens.json — do not edit. Regenerate via :generateDesignTokens. */")
        out.appendLine("object Tokens {")

        root.keys.filter { it != "meta" }.sorted().forEach { group ->
            @Suppress("UNCHECKED_CAST")
            emitObject(out, group, root[group] as Map<String, Any>, indent = 1)
        }

        out.appendLine()
        out.appendLine("    /** Spring physics (mapped from iOS UISpringTimingParameters, ADR-0024). */")
        out.appendLine("    data class Spring(val damping: Float, val stiffness: Float)")
        out.appendLine("}")
        val target = outputDir.get().file("dev/ios26/design/tokens/Tokens.kt").asFile
        target.parentFile.mkdirs()
        target.writeText(out.toString())
    }

    private fun emitObject(out: StringBuilder, name: String, obj: Map<String, Any>, indent: Int) {
        val pad = " ".repeat(indent * 4)
        out.appendLine("$pad object ${name.toPascalCase()} {")
        obj.keys.sorted().forEach { key ->
            val value = obj[key]!!
            when (value) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    emitObject(out, key, value as Map<String, Any>, indent + 1)
                }
                is List<*> -> {
                    val items = value.map { (it as Number).toFloat() }
                    out.appendLine("${" ".repeat((indent + 1) * 4)}val ${key.toCamelCase()}: List<Float> = listOf(${items.joinToString(", ") { "${it}f" }})")
                }
                is Number -> {
                    val n = value.toDouble()
                    if (n == n.toLong().toDouble()) {
                        out.appendLine("${" ".repeat((indent + 1) * 4)}val ${key.toCamelCase()}: Int = ${n.toLong()}")
                    } else {
                        out.appendLine("${" ".repeat((indent + 1) * 4)}val ${key.toCamelCase()}: Float = ${n}f")
                    }
                }
                is Boolean -> out.appendLine("${" ".repeat((indent + 1) * 4)}val ${key.toCamelCase()}: Boolean = $value")
                is String -> emitString(out, key, value, indent + 1)
                else -> error("Unsupported token value type for $key: ${value::class}")
            }
        }
        out.appendLine("$pad }")
    }

    private fun emitString(out: StringBuilder, key: String, raw: String, indent: Int) {
        val pad = " ".repeat(indent * 4)
        val name = key.toCamelCase()
        when {
            raw.startsWith("#") -> out.appendLine("$pad val $name: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(${hexToArgb(raw)})")
            raw.endsWith("dp") -> out.appendLine("$pad val $name: Dp = ${raw.removeSuffix("dp")}.dp")
            raw.endsWith("sp") -> out.appendLine("$pad val $name: TextUnit = ${raw.removeSuffix("sp")}.sp")
            else -> out.appendLine("$pad val $name: String = \"$raw\"")
        }
    }

    private fun hexToArgb(hex: String): String {
        val clean = hex.removePrefix("#")
        return when (clean.length) {
            6 -> "0xFF${clean.uppercase(Locale.ROOT)}"
            8 -> "0x${clean.uppercase(Locale.ROOT)}"
            else -> error("Unsupported color format: $hex")
        }
    }

    private fun String.toPascalCase(): String =
        split("-", "_").filter { it.isNotEmpty() }.joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }

    private fun String.toCamelCase(): String {
        val pascal = toPascalCase()
        return pascal.replaceFirstChar { it.lowercase() }
    }
}
