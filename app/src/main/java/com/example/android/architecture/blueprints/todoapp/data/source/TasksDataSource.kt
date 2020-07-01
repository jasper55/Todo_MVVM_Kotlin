package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
/**
 * Main entry point for accessing tasks data.
 *
 *
 * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
interface TasksDataSource {

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: Int): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: Int)

    suspend fun favorTask(task: Task)

    suspend fun unfavorTask(task: Task)

    suspend fun favorTask(taskId: Int)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: Int)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: Int)

    suspend fun setDueDate(task: Task, date: Long)

    suspend fun setTime(task: Task, timeLong: Long)

    suspend fun saveContactId(task: Task, id: String)
}
