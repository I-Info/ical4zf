package com.i1nfo

import com.i1nfo.com.i1nfo.ical4zf.model.CourseEvent
import com.i1nfo.ical4zf.model.ZfClassTable
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ComponentList
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.property.Action
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion
import okio.Buffer
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Duration
import java.time.LocalDate


@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Usage: java -jar ical4zf.jar <input file> <output file> <term start>")
        return
    }

    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<ZfClassTable>()

    val jsonFile = FileInputStream(args[0])

    val classTable = jsonAdapter.fromJson(Buffer().readFrom(jsonFile))
    val courses = classTable?.extractCourses() ?: return

    val alarm = VAlarm(Duration.ofMinutes(-5)).apply {
        add(Action(Action.VALUE_DISPLAY))
    }

    val termStart = LocalDate.parse(args[2])

    val vEvents = courses.map {
        CourseEvent(it, termStart).apply {
            add(alarm)
        }
    }

    val calendar = Calendar().apply {
        componentList = ComponentList(vEvents)
        add(ImmutableVersion.VERSION_2_0)
        add(ImmutableCalScale.GREGORIAN)
        add(ProdId("-//Course Event//i1nfo.com iCal4ZF//EN"))
    }

    println(calendar)

    println("File saved to ${args[1]}")
    val out = FileOutputStream(args[1])

    val outputter = CalendarOutputter()
    outputter.output(calendar, out)
}