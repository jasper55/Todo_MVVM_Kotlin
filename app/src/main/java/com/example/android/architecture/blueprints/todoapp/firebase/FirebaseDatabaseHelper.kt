package com.example.android.architecture.blueprints.todoapp.firebase

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDatabaseHelper{

    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private lateinit var todoList: List<Task>

    constructor(database: FirebaseDatabase, dbReference: DatabaseReference, todoList: List<Task>) {
        this.database = database
        this.dbReference = dbReference
        this.todoList = todoList
    }

    
}

