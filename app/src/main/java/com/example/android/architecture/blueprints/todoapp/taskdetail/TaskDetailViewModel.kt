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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.Manifest
import androidx.annotation.StringRes
import androidx.lifecycle.*

import android.app.Application
import android.content.Context
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.util.ContactBookService
import com.example.android.architecture.blueprints.todoapp.util.DateUtil
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.PermissionChecker
import kotlinx.coroutines.launch

/**
 * Listens to user actions from the list item in ([TasksFragment]) and redirects them to the
 * Fragment's actions listener.
 */
class TaskDetailViewModel(
        private val tasksRepository: TasksLocalDataSource,
        application: Application
) : AndroidViewModel(application) {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    var _dueDate = MutableLiveData<String>()
    var dueDate: LiveData<String> = _dueDate

    var _time = MutableLiveData<String>()
    var time: LiveData<String> = _time

    var _contactName = MutableLiveData<String>()
    var contactName: LiveData<String> = _contactName

    var _contactPermissionGranted = MutableLiveData<Boolean>()
    var contactPermissionGranted : LiveData<Boolean> = _contactPermissionGranted

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskCommand = MutableLiveData<Event<Unit>>()
    val editTaskCommand: LiveData<Event<Unit>> = _editTaskCommand

    private val _deleteTaskCommand = MutableLiveData<Event<Unit>>()
    val deleteTaskCommand: LiveData<Event<Unit>> = _deleteTaskCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = Transformations.map(_task) { input: Task? ->
        input?.isCompleted ?: false
    }

    val favorite: LiveData<Boolean> = Transformations.map(_task) { input: Task? ->
        input?.isFavorite ?: false
    }

    val taskId: String?
        get() = _task.value?.id

    fun deleteTask() = viewModelScope.launch {
        taskId?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskCommand.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskCommand.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun setFavored(favored: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (favored) {
            tasksRepository.favorTask(task)
            showSnackbarMessage(R.string.task_marked_favorite)
        } else {
            tasksRepository.unfavorTask(task)
            showSnackbarMessage(R.string.task_marked_unfavored)
        }
    }

    fun saveDueDate(dateLong: Long, date: String) {
        // needed otherwise view not updated
        _dueDate.value = date
        viewModelScope.launch {
            val task = _task.value ?: return@launch
            tasksRepository.setDueDate(task, dateLong)
        }
    }

    fun saveTime(timeLong: Long, time: String) {
        _time.value = time
        viewModelScope.launch {
            val task = _task.value ?: return@launch
            tasksRepository.setTime(task, timeLong)
        }
    }

    fun saveContactId(contactId: String) {
        viewModelScope.launch {
            val task = _task.value ?: return@launch
            tasksRepository.saveId(task, contactId)
        }
    }

    fun start(taskId: String?, context: Context) {
        _dataLoading.value = true

        // Espresso does not work well with coroutines yet. See
        // https://github.com/Kotlin/kotlinx.coroutines/issues/982
        EspressoIdlingResource.increment() // Set app as busy.

        viewModelScope.launch {
            if (taskId != null) {
                tasksRepository.getTask(taskId).let { result ->
                    if (result is Success) {
                        onTaskLoaded(result.data, context)
                    } else {
                        onDataNotAvailable(result)
                    }
                }
            }
            _dataLoading.value = false
            EspressoIdlingResource.decrement() // Set app as idle.
        }
    }

    private fun setTask(task: Task?, context: Context) {
        this._task.value = task
        _isDataAvailable.value = task != null
        _dueDate.value = DateUtil.parseFromLong(_task.value?.dueDate, getApplication())
        _time.value = DateUtil.parseTimeFromLong(_task.value?.time, getApplication())
        _contactPermissionGranted.value = PermissionChecker.checkPermission(PermissionChecker.REQUEST_CONTACTS_CODE,context)
        if (_contactPermissionGranted.value!!) {
            _contactName.value = _task.value?.contactId?.let { ContactBookService.getContactNameFromDB(it, context) }
        } else { showSnackbarMessage(R.string.no_contact_permission) }

        if (_contactName.value != null) {
            contactName = _contactName
        }
    }

    private fun onTaskLoaded(task: Task, context: Context) {
        setTask(task, context)
    }

    private fun onDataNotAvailable(result: Result<Task>) {
        _task.value = null
        _isDataAvailable.value = false
    }

    fun onRefresh(context: Context) {
        taskId?.let { start(it, context) }
    }

    fun onRefresh() {
        //taskId?.let { start(it, context) }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
