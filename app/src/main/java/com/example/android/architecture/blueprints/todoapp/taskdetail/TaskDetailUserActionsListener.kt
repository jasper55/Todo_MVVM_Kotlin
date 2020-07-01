package com.example.android.architecture.blueprints.todoapp.taskdetail


import android.content.Context
import android.view.View
import com.example.android.architecture.blueprints.todoapp.contacts.Contact

/**
 * Listener used with data binding to process user actions.
 */
interface TaskDetailUserActionsListener {
    fun onCompleteChanged(v: View)
    fun onFavoriteChanged(v: View)
    fun onDueDateChanged(v: View)
    fun onTimeChanged(v: View)
    fun onAddContactClicked(v: View)
}
