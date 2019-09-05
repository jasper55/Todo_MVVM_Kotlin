package com.example.android.architecture.blueprints.todoapp.UserRepository

class User @JvmOverloads constructor(
        var UserId: Int? = null,
        var userEmail: String? = null,
        var userPassword: String? = null)