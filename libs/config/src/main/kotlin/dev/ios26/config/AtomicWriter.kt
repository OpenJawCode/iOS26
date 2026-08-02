package dev.ios26.config

import dev.ios26.core.Log
import dev.ios26.core.NoopLog
import java.io.File

/**
 * Atomic file writer — temp + rename (same filesystem), so readers never observe
 * partial documents. The only write path into the store (ADR-0006).
 */
class AtomicWriter(
    private val dir: File,
    private val fileName: String,
    private val log: Log = NoopLog,
) {
    private val target: File get() = File(dir, fileName)
    private val temp: File get() = File(dir, "$fileName.tmp")

    fun write(content: String) {
        if (!dir.exists()) {
            check(dir.mkdirs()) { "Cannot create store dir: $dir" }
        }
        temp.writeText(content)
        if (!temp.renameTo(target)) {
            log.e(TAG, "Atomic rename failed: $temp -> $target")
            check(false) { "Atomic rename failed: $temp -> $target" }
        }
    }

    fun readOrNull(): String? = target.takeIf { it.exists() }?.readText()

    fun exists(): Boolean = target.exists()

    private companion object {
        const val TAG = "AtomicWriter"
    }
}
