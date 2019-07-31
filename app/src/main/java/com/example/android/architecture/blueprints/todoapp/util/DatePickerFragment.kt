package com.example.android.architecture.blueprints.todoapp.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker

import java.util.*


object DatePickerFragment{


    @SuppressLint("NewApi")
    fun createDialog(context: Context?) : DatePickerDialog  {
        val c: Calendar = Calendar.getInstance()
        val dYear: Int = c.get(Calendar.YEAR)
        val dMonth: Int = c.get(Calendar.MONTH)
        val dDay: Int = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePicker(context)
//        val listener = DatePickerDialog.OnDateSetListener((datePicker,dYear,dMonth,dDay)-> unit())

       // val dialog = DatePickerDialog(context, this, dYear, dMonth, dDay)
        val dialog = DatePickerDialog(context)

        return dialog
    }

    fun parseToString(year: Int, month: Int, day: Int): String{
        return "$day.$month.$year"
    }


    val c: Calendar = Calendar.getInstance()
    var dYear: Int = c.get(Calendar.YEAR)
    var dMonth: Int = c.get(Calendar.MONTH)
    var dDay: Int = c.get(Calendar.DAY_OF_MONTH)


    fun onCreateDialog(context: Context?): Dialog {
        return DatePickerDialog(context, datePickerListener, dYear, dMonth, dDay)
    }

    private val datePickerListener = DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
        dDay = selectedDay
        dMonth = selectedMonth
        dYear = selectedYear
    }

    fun getStringDate(context: Context?): String {
        val dialog = onCreateDialog(context)
        dialog.show()
        DatePickerDialog(context, datePickerListener, dYear, dMonth, dDay)
        return "$dDay.$dMonth.$dYear"
    }
    }
 /*
    , DatePickerDialog.OnDateSetListener {

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
}*/



