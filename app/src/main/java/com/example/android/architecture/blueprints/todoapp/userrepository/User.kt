package com.example.android.architecture.blueprints.todoapp.userrepository

data class User @JvmOverloads constructor(
        var uid: String? = null,
        var email: String? = null,
        var password: String? = null)