package com.example.android.architecture.blueprints.todoapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event

class LoginViewModel : ViewModel() {

    private val _openRegisterEvent = MutableLiveData<Event<Boolean>>()
    val openRegisterEvent: LiveData<Event<Boolean>> = _openRegisterEvent

    private val _openTaskListEvent = MutableLiveData<Event<String>>()
    val openTaskListEvent: LiveData<Event<String>> = _openTaskListEvent

    internal fun openRegisterFrag() {
        _openRegisterEvent.value = Event(true)
    }

    internal fun loginUser(userUid: String) {
        _openTaskListEvent.value = Event(userUid)
    }
}