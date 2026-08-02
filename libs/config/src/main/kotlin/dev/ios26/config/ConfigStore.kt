package dev.ios26.config

import dev.ios26.core.Clock
import dev.ios26.core.Log
import dev.ios26.core.NoopLog
import dev.ios26.core.SystemClock
import dev.ios26.schema.SchemaValidator
import dev.ios26.schema.Schemas
import java.io.File
import kotlinx.serialization.json.Json

/**
 * THE DEEP MODULE — ConfigStore. Small interface, all behavior behind it (ADR-0006).
 * Everything reads/writes config through this facade; nothing else touches the filesystem.
 *
 * Skeleton phase: store-root lifecycle + atomic writes + events dir. Schema shapes land
 * with their features (Phase 2+), each behind this same facade.
 */
class ConfigStore(
    val root: File,
    private val clock: Clock = SystemClock,
    private val log: Log = NoopLog,
) {
    private val json: Json = StoreJson.json
    private val storeRootSchema: String = Schemas.load("store-root.schema.json")
    private val rootWriter = AtomicWriter(root, STORE_ROOT_FILE, log)
    private val eventsDir: File get() = File(root, "shared/events")

    /** Creates the zone tree if absent. Idempotent. */
    fun ensureZones() {
        StoreLayout.zonesUnder(root).forEach { it.mkdirs() }
    }

    /** Writes the store root document; validates against the bundled schema first. */
    fun writeStoreRoot(storeRoot: StoreRoot) {
        val document = json.encodeToString(StoreRoot.serializer(), storeRoot)
        val violations = SchemaValidator(storeRootSchema).validate(document)
        check(violations.isEmpty()) { "StoreRoot violates schema: $violations" }
        rootWriter.write(document)
    }

    fun readStoreRoot(): StoreRoot? =
        rootWriter.readOrNull()?.let { document ->
            runCatching { json.decodeFromString(StoreRoot.serializer(), document) }
                .onFailure { log.e(TAG, "StoreRoot unparseable", it) }
                .getOrNull()
        }

    /** Atomic event write (ADR-0019). Writers may be system processes (hooks) or apps. */
    fun writeEvent(type: String) {
        val event = Event(type = type, ts = clock.nowMillis())
        AtomicWriter(eventsDir, "cc-open.json", log)
            .write(json.encodeToString(Event.serializer(), event))
    }

    fun eventFile(): File = File(eventsDir, "cc-open.json")

    private companion object {
        const val STORE_ROOT_FILE = "store-root.json"
        const val TAG = "ConfigStore"
    }
}
