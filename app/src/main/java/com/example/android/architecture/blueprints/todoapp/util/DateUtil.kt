package com.example.android.architecture.blueprints.todoapp.util

import java.text.SimpleDateFormat

object DateUtil {

    val myDate = "29.10.2014"
    //var sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    fun parseToLong(myDate: String): Long {
        var sdf = SimpleDateFormat("dd.MM.yyyy")
        var date = sdf.parse(myDate)
        return date.getTime()
    }

    fun parseToString(year: Int, month: Int, day: Int): String{
        return "$day.$month.$year"
    }

}
