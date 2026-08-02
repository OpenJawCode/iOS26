package dev.ios26.schema

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SchemaValidatorTest {

    private val storeRootSchema = Schemas.load("store-root.schema.json")

    @Test
    fun validStoreRootConforms() {
        val validator = SchemaValidator(storeRootSchema)
        val doc = """{"version":1,"zones":["system","shared","events"]}"""
        assertTrue("valid doc should conform", validator.isValid(doc))
    }

    @Test
    fun missingVersionRejected() {
        val validator = SchemaValidator(storeRootSchema)
        val doc = """{"zones":["system"]}"""
        assertFalse(validator.isValid(doc))
    }

    @Test
    fun zeroVersionRejected() {
        val validator = SchemaValidator(storeRootSchema)
        val doc = """{"version":0}"""
        assertFalse(validator.isValid(doc))
    }

    @Test
    fun unknownZoneRejected() {
        val validator = SchemaValidator(storeRootSchema)
        val doc = """{"version":1,"zones":["root"]}"""
        assertFalse(validator.isValid(doc))
    }
}
