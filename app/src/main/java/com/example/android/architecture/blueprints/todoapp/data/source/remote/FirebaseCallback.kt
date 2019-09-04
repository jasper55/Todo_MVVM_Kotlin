package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task

interface FirebaseCallback {
    fun onCallback(todoList: List<Task> )
}