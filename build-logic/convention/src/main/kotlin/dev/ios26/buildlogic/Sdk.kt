package dev.ios26.buildlogic

/**
 * API level policy — ADR-0017 + amendment (2026-08-02): runtime stays API 33 (minSdk =
 * targetSdk). compileSdk rose to 36 build-time only, required by androidx Compose BOM
 * 2026.06.01; no post-33 runtime API is used.
 */
internal object Sdk {
    const val COMPILE = 36
    const val MIN = 33
    const val TARGET = 33
}
