package dev.ios26.testing

import java.io.File
import java.nio.file.Files

/** Throwaway temp store for Tier-1 tests — mirrors ADR-0021 zone layout without SELinux. */
object TempStore {
    fun create(): File = Files.createTempDirectory("ios26-test-store").toFile()

    fun cleanup(root: File) {
        root.deleteRecursively()
    }
}

/** Creates a store directory tree (system/shared/events) inside a temp root. */
fun File.withZones(): File {
    File(this, "system").mkdirs()
    File(this, "shared").mkdirs()
    File(this, "shared/events").mkdirs()
    return this
}
