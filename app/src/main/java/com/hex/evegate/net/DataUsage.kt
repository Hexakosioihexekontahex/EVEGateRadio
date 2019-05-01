package com.hex.evegate.net

/**
 * The data usage that went over a threshold.
 */
class DataUsage internal constructor(
        /**
         * The type of data usage, can be [Type.DOWNLOAD] or [Type.UPLOAD].
         */
        val type: Type,
        /**
         * The number of bytes used in the period.
         */
        val bytes: Long,
        /**
         * The number of seconds for the time period.
         */
        val seconds: Int) {

    /**
     * Returns a useful warning message that you can use for logging, etc.
     *
     * @return the warning message
     */
    val warningMessage: String
        get() = "Warning! You have used $humanReadableSize in $humanReadableTimespan."

    /**
     * Returns the size in human-readable units, for example "10 kilobytes" or "12 gigabytes".
     *
     * @return the human-readable size
     */
    val humanReadableSize: String
        get() {
            if (bytes < 1000) {
                return "$bytes bytes"
            }
            if (bytes < 1000 * 1000) {
                return (bytes / 1000).toString() + " kilobytes"
            }
            return if (bytes < 1000 * 1000 * 1000) {
                (bytes / (1000 * 1000)).toString() + " megabytes"
            } else (bytes / (1000 * 1000 * 1000)).toString() + " gigabytes"
        }

    /**
     * Returns the timespan in human-readable units, for example "12 seconds" or "2 days".
     *
     * @return the human-readable timespan
     */
    val humanReadableTimespan: String
        get() {
            if (seconds < 60) {
                return "$seconds seconds"
            }
            if (seconds < 60 * 60) {
                return (seconds / 60).toString() + " minutes"
            }
            return if (seconds < 60 * 60 * 24) {
                (seconds / (60 * 60)).toString() + " hours"
            } else (seconds / (60 * 60 * 24)).toString() + " days"
        }

    enum class Type {
        DOWNLOAD, UPLOAD
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val usage = other as DataUsage?

        return bytes == usage!!.bytes && seconds == usage.seconds && type == usage.type
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (bytes xor bytes.ushr(32)).toInt()
        result = 31 * result + seconds
        return result
    }

    override fun toString(): String {
        return "$type $humanReadableSize in $humanReadableTimespan"
    }

    class Builder constructor(private val type: Type, private val bytes: Long) {
        private var seconds: Int = 0

        fun `in`(amount: Int, unit: TimeUnit): DataUsage {
            seconds = unit.of(amount)
            return DataUsage(type, bytes, seconds)
        }
    }

    companion object {

        fun download(amount: Int, unit: SizeUnit): Builder {
            return Builder(Type.DOWNLOAD, unit.of(amount.toLong()))
        }

        fun upload(amount: Int, unit: SizeUnit): Builder {
            return Builder(Type.UPLOAD, unit.of(amount.toLong()))
        }
    }
}
