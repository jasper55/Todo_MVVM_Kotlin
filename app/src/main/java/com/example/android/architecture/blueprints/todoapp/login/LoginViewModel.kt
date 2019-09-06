package com.example.android.architecture.blueprints.todoapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event

class LoginViewModel : ViewModel() {

    private val _openRegisterEvent = MutableLiveData<Event<Int>>()
    val openRegisterEvent: LiveData<Event<Int>> = _openRegisterEvent

    private val _openTaskListEvent = MutableLiveData<Event<Int>>()
    val openTaskListEvent: LiveData<Event<Int>> = _openTaskListEvent

    internal fun openRegisterFrag(userId: Int) {
        _openRegisterEvent.value = Event(userId)
    }

    internal fun loginUser(userId: Int) {
        _openTaskListEvent.value = Event(userId)
    }
}