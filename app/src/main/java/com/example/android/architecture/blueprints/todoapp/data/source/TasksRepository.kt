package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task

interface TasksRepository {

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun getTask(taskId: Int, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: Int)

    suspend fun favorTask(task: Task)

    suspend fun unfavorTask(task: Task)

    suspend fun favorTask(taskId: Int)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: Int)

    suspend fun setDueDate(task: Task, date: Long)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: Int)
}