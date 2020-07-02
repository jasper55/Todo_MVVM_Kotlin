package com.example.android.architecture.blueprints.todoapp.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.beforeTextChanged(beforeTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            beforeTextChanged.invoke(p0.toString())
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}