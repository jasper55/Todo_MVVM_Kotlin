package com.example.android.architecture.blueprints.todoapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.contacts.Contact
import com.example.android.architecture.blueprints.todoapp.userrepository.User

class RegisterViewModel : ViewModel() {

    val name = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _openLoginEvent = MutableLiveData<Event<Int>>()
    val openLoginEvent: LiveData<Event<Int>> = _openLoginEvent

    private val _user = MutableLiveData<User>().apply { value = User() }
    val user: LiveData<User> = _user

    internal fun openLoginFrag(userId: Int) {
        _openLoginEvent.value = Event(userId)
    }

    fun createUser() {
        saveUserToBundle()
    }

    private fun saveUserToBundle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}