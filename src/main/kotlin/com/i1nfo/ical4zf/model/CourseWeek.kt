package com.i1nfo.com.i1nfo.ical4zf.model

sealed interface CourseWeek {
    data class Single(val value: Long) : CourseWeek
    data class Range(
        val start: Long,
        val end: Long,
        /**
         * `true` for even week, `false` for odd week, `null` for not set.
         */
        val isEven: Boolean? = null,
    ) : CourseWeek {
        override fun toString(): String {
            return if (isEven == null) {
                "$start-$end"
            } else if (isEven) {
                "$start-${end}双"
            } else {
                "$start-${end}单"
            }
        }
    }
}