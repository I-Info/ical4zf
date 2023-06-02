package com.i1nfo.ical4zf.model

import com.i1nfo.com.i1nfo.ical4zf.model.Course
import com.i1nfo.com.i1nfo.ical4zf.model.CourseWeek
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.DayOfWeek

@JsonClass(generateAdapter = true)
data class ZfClassTable(
    val info: Info,
    val lessonsTable: List<LessonsTable>?,
    val practiceLessons: List<PracticeLesson>?,
) {
    @JsonClass(generateAdapter = true)
    data class Info(
        @Json(name = "ClassName")
        val className: String,
        @Json(name = "Name")
        val name: String
    )

    @JsonClass(generateAdapter = true)
    data class LessonsTable(
        val campus: String,
        val className: String,
        val classID: String,
        val credits: String,
        val id: String,
        val lessonHours: String,
        val lessonName: String,
        val lessonPlace: String,
        val placeID: String,
        val sections: String,
        val teacherName: String,
        val type: String,
        val week: String,
        val weekday: String
    ) {
        private fun parseWeekString(): List<CourseWeek> {
            val weeks: ArrayList<CourseWeek> = ArrayList()
            try {
                for (time in week.split(',')) {
                    var evenWeek: Boolean? = null
                    val clearTime = if (time.endsWith('周')) {
                        time.substring(0, time.length - 1)
                    } else if (time.endsWith("周(单)")) {
                        evenWeek = false
                        time.substring(0, time.length - 4)
                    } else if (time.endsWith("周(双)")) {
                        evenWeek = true
                        time.substring(0, time.length - 4)
                    } else {
                        time
                    }
                    val section = clearTime.split('-')
                    when (section.size) {
                        // Single week
                        1 -> weeks.add(CourseWeek.Single(clearTime.toLong()))
                        2 -> weeks.add(
                            CourseWeek.Range(
                                section[0].toLong(),
                                section[1].toLong(),
                                evenWeek,
                            )
                        )

                        else -> throw RuntimeException("Invalid week section format.")
                    }
                }
            } catch (e: NumberFormatException) {
                throw RuntimeException("Fail to parse week numbers.")
            }

            return weeks
        }

        fun toCourses(): List<Course> {
            val section = sections.split('-')
            if (section.size != 2) {
                throw RuntimeException("Invalid class section format.")
            }
            return parseWeekString().map {
                Course(
                    name = lessonName,
                    className = className,
                    teacherName = teacherName,
                    campus = campus,
                    place = lessonPlace,
                    type = type,
                    credits = credits.toFloat(),
                    hours = lessonHours.toInt(),
                    dayOfWeek = DayOfWeek.of(weekday.toInt()),
                    sectionStart = section[0].toInt(),
                    sectionEnd = section[1].toInt(),
                    week = it
                )
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class PracticeLesson(
        val className: String,
        val credits: String,
        val lessonName: String,
        val teacherName: String
    )

    fun extractCourses(): List<Course>? =
        lessonsTable?.flatMap(LessonsTable::toCourses)
}