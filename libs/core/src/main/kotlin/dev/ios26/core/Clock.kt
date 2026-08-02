package dev.ios26.core

/** Injectable time source — the only clock the store trusts. */
fun interface Clock {
    fun nowMillis(): Long
}

/** Production clock. */
object SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
