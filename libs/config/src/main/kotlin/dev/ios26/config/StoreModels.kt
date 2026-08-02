package dev.ios26.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Store root document — mirrors libs/schema store-root.schema.json (tests prove equivalence). */
@Serializable
data class StoreRoot(
    val version: Int = 1,
    val zones: List<String> = emptyList(),
)

@Serializable
data class Event(
    val type: String,
    val ts: Long,
)

object StoreJson {
    val json: Json = Json { encodeDefaults = true }
}
