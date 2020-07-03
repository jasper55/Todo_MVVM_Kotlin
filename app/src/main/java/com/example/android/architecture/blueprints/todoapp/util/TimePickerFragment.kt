package com.example.android.architecture.blueprints.todoapp.util

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

import java.util.*

class TimePickerFragment(val listener: TimePickerDialog.OnTimeSetListener) : DialogFragment() {

    val c = Calendar.getInstance()
    var hour = c.get(Calendar.HOUR_OF_DAY)
    var min = c.get(Calendar.MINUTE)


    fun showDialog(context: Context?) {
        val dialog = TimePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, listener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
        dialog.show()
    }

}
