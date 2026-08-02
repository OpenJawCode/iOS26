package dev.ios26.schema

import com.networknt.schema.Error
import com.networknt.schema.Schema
import com.networknt.schema.SchemaRegistry
import com.networknt.schema.SpecificationVersion
import tools.jackson.databind.ObjectMapper

/**
 * JSON Schema validator (D-P1.1: schema files are the source of truth; models are
 * proven equivalent by validation tests; codegen lands with the WebUI in Phase 6).
 * networknt 3.x + Jackson 3 (tools.jackson).
 */
class SchemaValidator(schemaJson: String) {

    private val mapper: ObjectMapper = ObjectMapper()
    private val schema: Schema =
        SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_7).getSchema(schemaJson)

    /** Returns validation messages; empty = document conforms. */
    fun validate(document: String): List<String> =
        schema.validate(mapper.readTree(document)).map(Error::getMessage)

    fun isValid(document: String): Boolean = validate(document).isEmpty()
}

/** Loads a bundled schema resource from the classpath (src/main/schemas). */
object Schemas {
    fun load(resourceName: String): String =
        checkNotNull(javaClass.getResourceAsStream("/$resourceName")) {
            "Schema resource missing: $resourceName"
        }.bufferedReader().use { it.readText() }
}
