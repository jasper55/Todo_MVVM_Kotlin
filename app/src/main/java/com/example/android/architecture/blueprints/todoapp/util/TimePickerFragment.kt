package com.example.android.architecture.blueprints.todoapp.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import java.util.*

class TimePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var year: Int = 0
    private var mon: Int = 0
    private var day: Int = 0
    private var dateTime = Calendar.getInstance()
    private var dateInMilliSeconds: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR);
        val mMonth: Int = c.get(Calendar.MONTH);
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH);

        // Get DatePicker Dialog
        return DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }


    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateTime.set(year, monthOfYear, dayOfMonth)
        dateInMilliSeconds = dateTime.getTimeInMillis()
        this.year = year
        this.mon = monthOfYear
        this.day = dayOfMonth
        // Do something with the time chosen by the user

        dateTime.set(year, monthOfYear, dayOfMonth);
        dateInMilliSeconds = dateTime.getTimeInMillis();
    }

    fun getLongFromTimePicker(): Long {
        return dateInMilliSeconds
    }

    fun getString(): String {
        return "$day + / + $mon + / + $year"
    }

    fun fromLongToDate(date: Long): String {
        return date.toString()
    }
}
