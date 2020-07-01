package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface TasksDao {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Tasks")
    suspend fun getTasks(): List<Task>

    /**
     * Select a task by uid.
     *
     * @param taskId the task uid.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    suspend fun updateTask(task: Task): Int

    /**
     * Update the complete status of a task
     *
     * @param taskId    uid of the task
     * @param completed status to be updated
     */
    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    suspend fun updateCompleted(taskId: Int, completed: Boolean)

    @Query("UPDATE tasks SET dueDate = :dueDate WHERE entryid = :taskId")
    suspend fun updateDate(taskId: Int, dueDate: Long)

    @Query("UPDATE tasks SET time = :time WHERE entryid = :taskId")
    suspend fun updateTime(taskId: Int, time: Long)

    @Query("UPDATE tasks SET favorite = :favorite WHERE entryid = :taskId")
    suspend fun updateFavorite(taskId: Int, favorite: Boolean)

    @Query("UPDATE tasks SET contactIdString = :contactIdString WHERE entryid = :taskId")
    suspend fun saveContactId(taskId: Int, contactIdString: String)
    /**
     * Delete a task by uid.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    suspend fun deleteTaskById(taskId: Int): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Tasks")
    suspend fun deleteTasks()

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("DELETE FROM Tasks WHERE completed = 1")
    suspend fun deleteCompletedTasks(): Int



}