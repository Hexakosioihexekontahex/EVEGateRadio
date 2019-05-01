package com.hex.evegate.net

/**
 *
 *
 * A threshold of data usage over a period of time. Data usage in a time period reaches this
 * threshold if either:
 *
 * a) The time span of data usage is over the threshold and the average rate of usage is higher than the threshold.
 *
 * b) The time span of data usage is under the threshold and the amount of data is over the threshold.
 */
class Threshold private constructor(
        /**
         * The number of bytes that must be reached to hit the threshold.
         */
        val bytes: Long,
        /**
         * The number of seconds that the bytes must be reached in.
         */
        val seconds: Int) {

    /**
     * Determines if the data usage has reached this threshold.
     *
     * @param usage the data usage to test
     * @return true if it reaches this threshold, false otherwise
     */
    fun hasReached(usage: DataUsage): Boolean {
        return !(bytes == -1L && seconds == -1) && (seconds >= usage.seconds && bytes <= usage.bytes || usage.seconds >= seconds && usage.bytes * seconds >= bytes * usage.seconds)
    }

    class Builder(private val size: Long) {

        /**
         * Set the time span that this threshold must be reached in.
         *
         * @param time the time span in the given unit
         * @param unit the unit the time span is given in
         * @return the threshold
         */
        fun per(time: Int, unit: TimeUnit): Threshold {
            return Threshold(size, unit.of(time))
        }

        /**
         * A convince method for [.per] for a time span of 1.
         *
         * @param unit the unit the time span is given in
         * @return the threshold
         */
        fun per(unit: TimeUnit): Threshold {
            return Threshold(size, unit.of(1))
        }
    }

    companion object {
        private val NONE = Threshold(-1, -1)

        /**
         * Constructs a Threshold that is reached when the given amount of data is used.
         *
         * @param size the size of data usage in the given unit
         * @param unit the unit the size is given in
         * @return a builder to chain the time span for the threshold
         */
        fun of(size: Long, unit: SizeUnit): Builder {
            return Builder(unit.of(size))
        }

        /**
         * Construct a Threshold that has no limit.
         *
         * @return the threshold
         */
        fun none(): Threshold {
            return NONE
        }
    }
}
