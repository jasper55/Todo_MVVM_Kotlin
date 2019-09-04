package com.example.android.architecture.blueprints.todoapp.contacts

import android.widget.ListView
import androidx.databinding.BindingAdapter

object ContactsListBindings {

        @BindingAdapter("app:items")
        @JvmStatic fun setItems(listView: ListView, items: List<Contact>) {
            with(listView.adapter as ContactsAdapter) {
                replaceData(items)
            }
        }
}