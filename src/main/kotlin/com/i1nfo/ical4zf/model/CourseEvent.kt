package com.i1nfo.com.i1nfo.ical4zf.model

import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.WeekDay
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.transform.recurrence.Frequency
import java.time.LocalDate
import java.time.LocalDateTime

class CourseEvent(course: Course, termStart: LocalDate) : VEvent() {
    init {
        when (course.week) {
            is CourseWeek.Single -> {
                val day = termStart.plusWeeks(course.week.value - 1).plusDays(course.dayOfWeek.ordinal.toLong())
                val start = Section.PAIRS[course.sectionStart - 1].first
                val end = Section.PAIRS[course.sectionEnd - 1].second
                add(DtStart(day.atTime(start)))
                add(DtEnd(day.atTime(end)))
            }

            is CourseWeek.Range -> {
                val startDay = if (course.week.isEven == null || course.week.isEven == (course.week.start % 2 == 0L)) {
                    termStart.plusWeeks(course.week.start - 1)
                } else {
                    termStart.plusWeeks(course.week.start)
                }.plusDays(course.dayOfWeek.ordinal.toLong())
                val endDay = termStart.plusWeeks(course.week.end - 1).plusDays(course.dayOfWeek.ordinal.toLong())
                val start = Section.PAIRS[course.sectionStart - 1].first
                val end = Section.PAIRS[course.sectionEnd - 1].second
                add(DtStart(startDay.atTime(start)))
                add(DtEnd(startDay.atTime(end)))
                val recur = Recur.Builder<LocalDateTime>()
                    .frequency(Frequency.WEEKLY)
                    .until(endDay.atTime(23, 59, 59))
                    .dayList(listOf(WeekDay.getWeekDay(course.dayOfWeek)))
                    .interval(if (course.week.isEven == null) 1 else 2)
                    .build()
                add(RRule(recur))
            }
        }
        add(Summary(course.summary))
        add(Description(course.description))
        add(Location(course.location))
        add(Uid("${course.hashCode()}@i1nfo.com"))
    }
}