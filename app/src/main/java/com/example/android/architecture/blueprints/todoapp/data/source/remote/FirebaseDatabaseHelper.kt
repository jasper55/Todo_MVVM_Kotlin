package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.google.common.reflect.TypeToken
import com.google.firebase.database.*
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseDatabaseHelper {

    private var database: FirebaseDatabase
    private var dbReference: DatabaseReference

    constructor() {
        this.database = FirebaseDatabase.getInstance()
        this.dbReference = this.database.getReference("Tasks")
    }

    private fun convertFromJSONStringToClass(jsonString: String): List<Task> {
        var gson = Gson()
        val objectList = gson.fromJson(jsonString, Array<Task>::class.java).asList()
        return objectList
    }

    private fun convertFromJSONStringToClass2(jsonString: String): List<Task> {
        var gson = Gson()
        val list: List<Task> = gson.fromJson(jsonString, object : TypeToken<List<Task>>() {}.type)
        return list
    }

    private fun convertDataClassToJSONString(task: Task): String {
        var gson = Gson()
        var jsonString = gson.toJson(Task(task.title, task.description, task.isCompleted, task.isFavorite, task.dueDate, task.time, task.contactIdString))
        return jsonString
    }

    fun saveJSONString(todoList: List<Task>): String {
        """{"uid":1,"description":"Test"}"""

        """"{
            "foos" : [{
            "prop1":"value1",
            "prop2":"value2"
        }, {
            "prop1":"value3",
            "prop2":"value4"
        }]
        }"""

        var jsonString = ""
        for (task in todoList) {
            var string = convertDataClassToJSONString(task)
            jsonString.plus(string)
        }
        return jsonString
    }

    fun storeJSONString(todoList: List<Task>): JSONObject {

        val array = JSONArray()
        for (task in todoList) {
            array.put(convertDataClassToJSONString(task))
        }
        val jsonObject = JSONObject()
        jsonObject.put("task", array)
        return jsonObject
    }

    suspend fun readTasks(): List<Task> =
        suspendCoroutine { cont ->
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val todoList = ArrayList<Task>()
                    for (task in dataSnapshot.children) {
                        val it = task.getValue(Task::class.java) as Task
                        todoList.add(it)
                    }
//                    firebaseCallback.onCallback(todoList)
                    cont.resume(todoList)
                }

                override fun onCancelled(p0: DatabaseError) {
                    val emptyList: List<Task> = emptyList()
                    cont.resume(emptyList)

                    Timber.i("some error occurred while loading data from firebase db")
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }


            dbReference.addListenerForSingleValueEvent(listener)



    }

    suspend fun saveToDatabase(todoList: List<Task>) {
        todoList.forEach {
            dbReference.child("task ${it.id}").setValue(it)
        }
        Timber.i("Firebase database updated")
    }

    suspend fun deleteAllTasks() {
        dbReference.database.getReference("Tasks").removeValue()
        Timber.i("Firebase data deleted")
    }

    fun deleteTask(taskId: Int) {
        dbReference.child("task $taskId").removeValue()
    }

}

