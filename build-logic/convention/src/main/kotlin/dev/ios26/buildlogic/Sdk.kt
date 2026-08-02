package dev.ios26.buildlogic

/** API level policy — ADR-0017: minSdk = targetSdk = compileSdk = 33, frozen to the platform. */
internal object Sdk {
    const val COMPILE = 33
    const val MIN = 33
    const val TARGET = 33
}
