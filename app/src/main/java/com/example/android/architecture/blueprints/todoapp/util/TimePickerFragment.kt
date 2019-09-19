package com.example.android.architecture.blueprints.todoapp.util

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.fragment.app.DialogFragment

import java.util.*


class TimePickerFragment : DialogFragment() {

    val c = Calendar.getInstance()
    var hour = c.get(Calendar.HOUR_OF_DAY)
    var min = c.get(Calendar.MINUTE)

    private val timePickerListener = TimePickerDialog.OnTimeSetListener { _picker, hourOfDay, minute ->
        hour = hourOfDay
        min = minute
    }

    fun showDialog(context: Context?) {
        val dialog = TimePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, timePickerListener, hour, min, true)
        dialog.show()
    }

    fun getTime(): String {
        return "$hour:$min"
    }
}