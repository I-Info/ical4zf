package com.i1nfo.com.i1nfo.ical4zf.model

import java.time.DayOfWeek

data class Course(
    val name: String,
    val teacherName: String,
    val campus: String,
    val place: String,
    val className: String,
    val type: String,
    val credits: Float,
    val hours: Int,
    val sectionStart: Int,
    val sectionEnd: Int,
    val dayOfWeek: DayOfWeek,
    val week: CourseWeek,
) {
    val summary: String
        get() = "$teacherName-$name"

    val location: String
        get() = "$campus-$place"

    val description: String
        get() = "$className-$type-$credits-$hours"
}