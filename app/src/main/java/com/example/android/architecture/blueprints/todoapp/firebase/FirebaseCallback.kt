package com.example.android.architecture.blueprints.todoapp.firebase

import com.example.android.architecture.blueprints.todoapp.data.Task

interface FirebaseCallback {
    fun onCallback(todoList: List<Task> )
}