package com.hex.evegate.net

/**
 * A size unit with the base of bytes.
 */
enum class SizeUnit constructor(private val scale: Int) {
    BYTE(1), BYTES(1),
    KILOBYTE(1000), KILOBYTES(1000),
    MEGABYTE(1000 * 1000), MEGABYTES(1000 * 1000),
    GIGABYTE(1000 * 1000 * 1000), GIGABYTES(1000 * 1000 * 1000);

    /**
     * Converts the value from this unit to bytes
     *
     * @param value the size in this unit
     * @return the size in bytes
     */
    fun of(value: Long): Long {
        return value * scale
    }
}
