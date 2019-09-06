package com.example.android.architecture.blueprints.todoapp.userrepository

data class User @JvmOverloads constructor(
        var id: Int? = 0,
        var email: String? = null,
        var password: String? = null)