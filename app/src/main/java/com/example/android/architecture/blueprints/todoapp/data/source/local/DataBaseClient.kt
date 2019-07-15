package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.content.Context
import androidx.room.Room


class DatabaseClient private constructor(private val mCtx: Context) {

    //our app database object
    val appDatabase: ToDoDatabase

    init {

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, ToDoDatabase::class.java!!, "myTodoDB").build()
    }

    companion object {
        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance as DatabaseClient
        }
    }
}