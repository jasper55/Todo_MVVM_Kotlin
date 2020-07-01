package com.example.android.architecture.blueprints.todoapp.tasks

import android.view.View

import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Listener used with data binding to process user actions.
 */
interface TaskItemUserActionsListener {
    fun onCompleteChanged(task: Task, v: View)
    fun onFavoriteChanged(task: Task, v: View)
    fun onTaskClicked(task: Task)
}
