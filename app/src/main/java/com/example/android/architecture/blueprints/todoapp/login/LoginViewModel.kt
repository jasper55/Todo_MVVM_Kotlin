package com.example.android.architecture.blueprints.todoapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event

class LoginViewModel : ViewModel() {

    private val _goToRegisterEvent = MutableLiveData<Event<Int>>()
    val goToRegisterEvent: LiveData<Event<Int>> = _goToRegisterEvent

    private val _goToTasksActivity = MutableLiveData<Event<Int>>()
    val goToTasksActivity: LiveData<Event<Int>> = _goToTasksActivity

    internal fun goToRegisterFrag(userId: Int) {
        _goToRegisterEvent.value = Event(userId)
    }

    internal fun goToTasksActivity(userId: Int) {
        _goToTasksActivity.value = Event(userId)
    }

    fun login(userId: Int){
        goToTasksActivity(userId)
    }



}