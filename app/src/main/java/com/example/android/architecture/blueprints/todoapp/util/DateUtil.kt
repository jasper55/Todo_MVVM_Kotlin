package com.example.android.architecture.blueprints.todoapp.util

import android.content.Context
import com.example.android.architecture.blueprints.todoapp.R
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    //var sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    fun parseToLong(myDate: String): Long {
        var sdf = SimpleDateFormat("dd.MM.yyyy")
        var date = sdf.parse(myDate)
        return date.getTime()
    }

    fun parseToString(year: Int, month: Int, day: Int): String{
        return "$day.$month.$year"
    }

    fun parseFromLong(dueDate: Long?, context: Context): String? {
        val long = dueDate!!
        if (long == 0L) {
            return context.getResources().getString(R.string.no_due_date_set)
        }
        val d = Date(dueDate!!)
        var sdf = SimpleDateFormat("dd.MM.yyyy")
        return  sdf.format(d)
    }

}
