package dev.ios26.core

/**
 * Minimal logger abstraction (ADR-0018: no third-party SDKs; observability stays on-device).
 * Production wiring (logcat + store logs) lands with the companion diagnostics (Phase 5).
 */
interface Log {
    fun i(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/** No-op logger — safe default for libraries; test-friendly. */
object NoopLog : Log {
    override fun i(tag: String, message: String) = Unit
    override fun e(tag: String, message: String, throwable: Throwable?) = Unit
}
