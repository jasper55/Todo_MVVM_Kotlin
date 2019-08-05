package com.example.android.architecture.blueprints.todoapp.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker

import java.util.*


object DatePickerFragment {

    val c: Calendar = Calendar.getInstance()
    var dYear: Int = c.get(Calendar.YEAR)
    var dMonth: Int = c.get(Calendar.MONTH)
    var dDay: Int = c.get(Calendar.DAY_OF_MONTH)

    private val datePickerListener = DatePickerDialog.OnDateSetListener { _picker, selectedYear, selectedMonth, selectedDay ->
        dDay = selectedDay
        dMonth = selectedMonth
        dYear = selectedYear
    }

    fun showDialog(context: Context?) {
        val dialog = DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, datePickerListener, dYear, dMonth, dDay) //DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, datePickerListener, dYear, dMonth, dDay)
        dialog.show()
    }

    fun getDate(): String {
        return "$dDay.${dMonth + 1}.$dYear"
    }
}