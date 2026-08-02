package dev.ios26.config

/**
 * Store zone layout (ADR-0021): system zone is root/system-only; shared zone carries
 * cross-component config and events under a dedicated SELinux context (Phase 4 sepolicy).
 */
object StoreLayout {
    const val ROOT = "/data/adb/ios26"
    const val SYSTEM = "$ROOT/system"
    const val SHARED = "$ROOT/shared"
    const val EVENTS = "$SHARED/events"

    /** All zones the store owns; created at provision time (Magisk module, Phase 4). */
    val zones: List<String> = listOf(SYSTEM, SHARED, EVENTS)

    /** Test-relative layout (no SELinux on AVD/Tier-1). */
    fun zonesUnder(root: java.io.File): List<java.io.File> =
        listOf("system", "shared", "shared/events").map { java.io.File(root, it) }
}
