package com.example.android.architecture.blueprints.todoapp.register

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.userrepository.User
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    val user: MutableLiveData<User> = MutableLiveData()

    init {
        user.value = User()
    }

    private val _openLoginEvent = MutableLiveData<Event<String>>()
    val openLoginEvent: LiveData<Event<String>> = _openLoginEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val _errorMessageEvent = MutableLiveData<Event<String>>()
    val errorMessageEvent: LiveData<Event<String>> = _errorMessageEvent


    internal fun openLoginFrag(userUid: String) {
        _openLoginEvent.value = Event(userUid)
    }

    fun registerUser() {
        val email: String? = user.value?.email
        val password: String? = user.value?.password

        Log.i("Register:", "User with ${email} created")

        if(email == null || password == null) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                        return@addOnCompleteListener

                    // else if
                    showSnackbarMessage(R.string.user_created)
                    val userUid = it.result?.user?.uid!!
                    user.value?.uid = userUid
                    openLoginFrag(userUid)
                    Log.i("Register:", "User with ${it.result?.user?.uid} created")
                }
                .addOnFailureListener {
                    showErrorMessage("Failed to register: ${it.message}")
                    Log.i("Register:", "Failed to register: ${it.message}")

                }
        //saveUserToPrefs()
    }
    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun showErrorMessage(message: String) {
        _errorMessageEvent.value = Event(message)
    }

    private fun saveUserToPrefs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}