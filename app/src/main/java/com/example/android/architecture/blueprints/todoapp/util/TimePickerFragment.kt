package com.example.android.architecture.blueprints.todoapp.util

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var date: Long = 0
    private var hour: Int = 0
    private var min: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        date = hourOfDay.toLong() + minute.toLong()
        hour = hourOfDay
        min = minute
        // Do something with the time chosen by the user
    }

    fun getLongFromTimePicker(): Long {
        return date
    }

    fun getString(): String {
        return "$hour + : + $min"
    }

    fun fromLongToDate(date: Long): String {
        return date.toString()
    }
}
