/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.google.common.collect.Lists
import kotlinx.coroutines.delay

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<Int, Task>(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override suspend fun getTasks(): Result<List<Task>> {
        // Simulate network by delaying the execution.
        val tasks = Lists.newArrayList(TASKS_SERVICE_DATA.values)
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(tasks)
    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override suspend fun getTask(taskId: Int): Result<Task> {

        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        TASKS_SERVICE_DATA[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Task not found"))
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description, false, false, 0, 0, "")
        TASKS_SERVICE_DATA.put(newTask.id!!, newTask)
    }
    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id!!, task)
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.isFavorite, task.dueDate, task.time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, completedTask)
    }

    override suspend fun completeTask(taskId: Int) {
        // Not required for the remote data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun favorTask(task: Task) {
        val favorTask = Task(task.title, task.description, task.isCompleted, true, task.dueDate, task.time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, favorTask)
    }

    override suspend fun favorTask(taskId: Int) {
        // Not required for the remote data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun unfavorTask(task: Task) {
        val unfavorTask = Task(task.title, task.description, task.isCompleted, false, task.dueDate, task.time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, unfavorTask)
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.isFavorite, task.dueDate, task.time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, activeTask)
    }

    override suspend fun activateTask(taskId: Int) {
        // Not required for the remote data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<Int, Task>
    }

    override suspend fun setDueDate(task: Task, date: Long) {
        val dueDateTask = Task(task.title, task.description, task.isCompleted, task.isFavorite, date, task.time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, dueDateTask)
    }

    override suspend fun setTime(task: Task, time: Long) {
        val timeTask = Task(task.title, task.description, task.isCompleted, task.isFavorite, task.dueDate, time, task.contactIdString)
        TASKS_SERVICE_DATA.put(task.id!!, timeTask)
    }

    override suspend fun saveContactId(task: Task, contactId: String) {
        val timeTask = Task(task.title, task.description, task.isCompleted, task.isFavorite, task.dueDate, task.time, contactId)
        TASKS_SERVICE_DATA.put(task.id!!, timeTask)
    }


    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: Int) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}
