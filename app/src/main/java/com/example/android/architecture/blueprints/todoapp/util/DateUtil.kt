package com.example.android.architecture.blueprints.todoapp.util

import android.content.Context
import com.example.android.architecture.blueprints.todoapp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {

    val dateFormatter = SimpleDateFormat("dd.MM.yyyy")

    fun parseToLong(myDate: String): Long {

        var date = dateFormatter.parse(myDate)
        return date.getTime()
    }

    fun parseToString(year: Int, month: Int, day: Int): String {
        return "$day.${month + 1}.$year"
    }

    fun parseFromLong(dueDate: Long?, context: Context): String? {
        val long = dueDate!!
        if (long == 0L) {
            return context.getResources().getString(R.string.no_due_date_set)
        }
        val d = Date(dueDate!!)
        return dateFormatter.format(d)
    }

    fun getTimeRemainig(dueDate: Long): String {
        val days = getDaysRemaining(dueDate)
        if (days >= 0) { return "$days days remaining" }
            return "${-days} days exceeded"
    }

    fun isExpired(dueDate: Long): Boolean {
        val days = getDaysRemaining(dueDate)
        if (days >= 0) { return false }
            return true
    }

    private fun getDaysRemaining(dueDate: Long): Long {
        val c: Calendar = Calendar.getInstance()
        val dYear: Int = c.get(Calendar.YEAR)
        val dMonth: Int = c.get(Calendar.MONTH)
        val dDay: Int = c.get(Calendar.DAY_OF_MONTH)

        val date = parseToString(dYear, dMonth, dDay)
        val diff = dueDate - parseToLong(date)

        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        return days
    }
}
