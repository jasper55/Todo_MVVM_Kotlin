package com.example.android.architecture.blueprints.todoapp.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.login.LoginFragment
import com.google.android.material.navigation.NavigationView

// register User - 15:22

//if email, pass is empty 17:08

// failure auth  18:45

// Login Activity 21:17
// method still has to be implementen like the register action

// add circular text field
// start 22:34

// xml for rounded shape 24:18

// anwendung des shapes:
// android:background:"@drawable/....xml_name"

// colored rounded container - 25:42
// new drawable-> name

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        if (savedInstanceState != null) {
            return
        }

        val navController : NavController = findNavController(R.id.nav_host_fragment_new)


        val loginFragment = LoginFragment()
        loginFragment.arguments = intent.extras

       supportFragmentManager.beginTransaction().add(R.id.start_activity, loginFragment)
    }




}