/*
 * Copyright 2017, The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.architecture.blueprints.todoapp.util.DatePickerFragment
import com.example.android.architecture.blueprints.todoapp.util.DateUtil

/**
 * Immutable model class for a Task. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param title       title of the task
 * @param description description of the task
 * @param isCompleted whether or not this task is completed
 * @param uid          uid of the task
 * @param dueDate      due date of the task
 * @param time         expiry time of the task
 * @param connectedIDString connectetd contacts of the task
 */
@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
        @ColumnInfo(name = "favorite") var isFavorite: Boolean = false,
        @ColumnInfo(name = "dueDate") var dueDate: Long = 0L,
        @ColumnInfo(name = "time") var time: Long = 0L,
        @ColumnInfo(name = "contactIdString") var contactIdString: String = ""
) {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "entryid") var id: Int = 0

    // is being accessed by the OverViewFragment
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    val timeRemaining: String
            get() = DateUtil.getTimeRemainig(dueDate)

    val isExpired: Boolean
        get() = DateUtil.isExpired(dueDate)

}