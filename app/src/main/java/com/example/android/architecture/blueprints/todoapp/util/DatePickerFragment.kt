package com.example.android.architecture.blueprints.todoapp.util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context

import java.util.*


class DatePickerFragment(val listener: DatePickerDialog.OnDateSetListener) {

    val c: Calendar = Calendar.getInstance()
    var dYear: Int = c.get(Calendar.YEAR)
    var dMonth: Int = c.get(Calendar.MONTH)
    var dDay: Int = c.get(Calendar.DAY_OF_MONTH)


    fun showDialog(context: Context?) {
        val dialog = DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, listener, dYear, dMonth, dDay) //DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, datePickerListener, dYear, dMonth, dDay)
        dialog.show()
    }

    fun getCurrentDate(): Long{
        return (dYear+ dMonth+ dDay).toLong()
    }
}