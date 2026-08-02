package dev.ios26.hooks.api

/**
 * HOOKS SEAM (ADR-0019/0033) — the contract between the LSPosed layer and the app layer.
 * Hooks implement capabilities, emit events; hosts subscribe. No cross-surface coupling.
 */

/** A hook surface capability — one per surface (ADR-0007). */
interface HookCapability {
    val surfaceName: String
    /** Apply all hooks for this surface. Throwing disables the whole surface (all-or-nothing). */
    fun apply(context: HookContext)
}

/** Minimal context handed to a surface hook. */
class HookContext(
    val classLoader: ClassLoader,
    val packageName: String,
    val flags: FeatureFlags,
)

/** File-based feature flags (ADR-0035): default OFF. Absent/missing file = everything off. */
class FeatureFlags(private val basePath: String) {
    fun isEnabled(surface: String): Boolean =
        runCatching { java.io.File(basePath, "$surface.flag").exists() }.getOrDefault(false)
}

/** Typed event names (ADR-0019) — written to the shared-store events dir by hooks. */
object HookEvents {
    const val CC_OPEN = "cc-open"
    const val CC_CLOSE = "cc-close"
}
