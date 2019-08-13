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
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.DateUtil
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Immutable model class for a Task. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param title       title of the task
 * @param description description of the task
 * @param isCompleted whether or not this task is completed
 * @param id          id of the task
 */
@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
        @ColumnInfo(name = "favorite") var isFavorite: Boolean = false,
        @ColumnInfo(name = "dueDate") var dueDate: Long = 0L,
        @ColumnInfo(name = "time") var time: Long = 0L,
        @ColumnInfo(name = "contactIdString") var contactIdString: String = "",
        @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString()
) {

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

    // Declare Compators
    //var SORT_BY_ID: Comparator<Task> = Comparator<Task> { item1, item2 -> (item1.id.toInt() - item2.id.toInt()) }

//    var SORT_BY_NAME: Comparator<Task> = Comparator<Task> { item1, item2 ->
//        val i1 = item1.title.toLowerCase()
//        val i2 = item2.title.toLowerCase()
//        i1.compareTo(i2)
//    }

/*    var SORT_BY_DONE: Comparator<Task> = Comparator<Task> { item1, item2 ->
        val b1 = item1.isCompleted
        val b2 = item2.isCompleted

        if (b1 == !b2) {
            return@Comparator 1
        }
        if (!b1 == b2) {
            -1
        } else 0
    }*/

/*    var SORT_BY_PRIORITY: Comparator<Task> = Comparator<Task> { item1, item2 ->
        val b1 = item1.isFavorite
        val b2 = item2.isFavorite

        if (b1 == !b2) {
            return@Comparator 1
        }
        if (!b1 == b2) {
            -1
        } else 0
    }*/

    //var SORT_BY_DATE: Comparator<Task> = Comparator<Task> { item1, item2 -> (item1.dueDate.toInt() - item2.dueDate.toInt()) }

}