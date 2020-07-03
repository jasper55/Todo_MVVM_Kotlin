package com.example.android.architecture.blueprints.todoapp.login

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.userrepository.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _openRegisterEvent = MutableLiveData<Event<Boolean>>()
    val openRegisterEvent: LiveData<Event<Boolean>> = _openRegisterEvent

    private val _openTaskListEvent = MutableLiveData<Event<String>>()
    val openTaskListEvent: LiveData<Event<String>> = _openTaskListEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _snackbarMessage = MutableLiveData<Event<String>>()
    val snackbarMessage: LiveData<Event<String>> = _snackbarMessage

    private val _errorMessageEvent = MutableLiveData<Event<String>>()
    val errorMessageEvent: LiveData<Event<String>> = _errorMessageEvent

    private val _isInternetAvailable = MutableLiveData<Boolean>()
    val isInternetAvailable: LiveData<Boolean> = _isInternetAvailable

    private val _areLoginInFieldCorrect = MutableLiveData<Boolean>()
    val areLoginInFieldCorrect: LiveData<Boolean> = _areLoginInFieldCorrect

    private val _loginIsIdle = MutableLiveData<Boolean>()
    val loginIsIdle: LiveData<Boolean> = _loginIsIdle


    private val _loginErrorMessage = MutableLiveData<String>()
    val loginErrorMessage: LiveData<String> = _loginErrorMessage

    val user: MutableLiveData<User> = MutableLiveData()

    init {
        user.value = User()
    }

    internal fun openRegisterFrag() {
        _openRegisterEvent.value = Event(true)
    }

    internal fun loginUser(userUid: String) {
        val email: String? = user.value?.email
        val password: String? = user.value?.password

        if (email == null || password == null) return
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if
                //showSnackbarMessage(R.string.user_created)
                val userUid = it.result?.user?.uid!!
            }
        _openTaskListEvent.value = Event(userUid)
    }

    internal fun loginUser() {
        val email: String? = user.value?.email
        val password: String? = user.value?.password

        if (email == null || password == null) {
            if (email == null || password == null) {
                _loginErrorMessage.value = "fill in your login details"
            }
            if (email == null && !password.isNullOrBlank()) {
                _loginErrorMessage.value = "please fill in your email address"
            }
            if (password == null && !email.isNullOrBlank()) {
                _loginErrorMessage.value = "no password entered, please fill in your password"
            }
            _areLoginInFieldCorrect.value = false
            return
        } else {
            _loginIsIdle.value = true
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if
                    val userUid = it.result?.user?.uid!!
                    showSnackbarText(R.string.user_logged_in)
                    _openTaskListEvent.value = Event(userUid)
                    Log.i("Login:", "Login succesful")
                    _areLoginInFieldCorrect.value = true
                    Thread.sleep(5000)
                }
                .addOnFailureListener {
                    _areLoginInFieldCorrect.value = false
//                showErrorMessage("Failed to login: ${it.message}")
                    _loginErrorMessage.value = it.message
                    Log.i("Login:", "Failed to login: ${it.message}")

                }
            _loginIsIdle.value = false
        }
    }

    fun checkNetworkConnection(activity: AppCompatActivity): Boolean {
        _isInternetAvailable.value =
            (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        return isInternetAvailable.value!!
    }

    fun displayEmailInvalidPrompt() {

    }

    private fun showSnackbarText(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun showErrorMessage(message: String) {
        _errorMessageEvent.value = Event(message)
    }


}