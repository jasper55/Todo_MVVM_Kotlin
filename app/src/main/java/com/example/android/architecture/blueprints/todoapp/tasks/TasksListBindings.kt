package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.databinding.BindingAdapter
import android.widget.ListView

import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Contains [BindingAdapter]s for the [Task] list.
 */
object TasksListBindings {

    @BindingAdapter("app:items")
    @JvmStatic fun setItems(listView: ListView, items: List<Task>) {
        with(listView.adapter as TasksAdapter) {
            replaceData(items)
        }
    }
}
