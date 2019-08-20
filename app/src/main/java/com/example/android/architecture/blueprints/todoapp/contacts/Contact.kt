package com.example.android.architecture.blueprints.todoapp.contacts


data class Contact @JvmOverloads constructor(
    var taskId: String? = null,
    var contactId: String? = null,
    var contactName: String? = null,
    var contactEmail: String? = null,
    var contactPhoneNumber: String? = null)

