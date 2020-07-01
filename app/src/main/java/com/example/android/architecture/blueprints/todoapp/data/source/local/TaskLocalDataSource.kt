package com.example.android.architecture.blueprints.todoapp.data.source.local

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource internal constructor(
        private val tasksDao: TasksDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.getTasks())
        } catch(e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTask(taskId: Int): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: Int) {
        // Not required for the local data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun favorTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateFavorite(task.id, true)
    }

    override suspend fun unfavorTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateFavorite(task.id, false)
    }

    override suspend fun favorTask(taskId: Int) {
        // Not required for the local data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: Int) {
        // Not required for the local data source because the {@link DefaultTasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }

    override suspend fun setDueDate(task: Task, date: Long) = withContext(ioDispatcher) {
        tasksDao.updateDate(task.id, date)
    }

    override suspend fun setTime(task: Task, time: Long) = withContext(ioDispatcher) {
        tasksDao.updateTime(task.id, time)
    }

    override suspend fun saveContactId(task: Task, contactId: String) {
        tasksDao.saveContactId(task.id, contactId)
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        tasksDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: Int) = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteTaskById(taskId)
    }


}
