package com.example.android.architecture.blueprints.todoapp.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.LoginFragmentBinding
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.onTextChanged
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var viewDataBinding: LoginFragmentBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        viewModel = obtainViewModel(LoginViewModel::class.java)
        viewDataBinding = LoginFragmentBinding.bind(root).apply {
            viewmodel = viewModel
            listener = object : UserActionsNavigationListener {

                override fun onLoginClicked() {
                    viewDataBinding.viewmodel?.loginUser()
                }

                override fun onRegisterClicked() {
                    viewDataBinding.viewmodel?.openRegisterFrag()
                }
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupNavigation()
        setupSnackbar(Snackbar.LENGTH_SHORT)
        checkIfInternetAvailable()
        checkIfUserIsAlreadyLoggedIn()
        listenForLoginFieldChanges()
        listenForLoginResponse()
    }

    private fun listenForLoginResponse() {
        viewModel.loginIsIdle.observe(this, Observer {
            if (it) {
                hideUIAndShowProgressBar()
            } else {
                hideProgressBar()
            }
        })

    }

    private fun listenForLoginFieldChanges() {
        viewModel.areLoginInFieldsCorrect.observe(this, Observer {
            if (it == false) {
                hideKeyboard(context!!)
                viewDataBinding.errorPrompt.visibility = View.VISIBLE
                viewDataBinding.errorPrompt.text = viewModel.loginErrorMessage.value
                viewDataBinding.errorPrompt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            } else {
                viewDataBinding.errorPrompt.visibility = View.GONE
            }
        })

        viewDataBinding.loginEmail.onTextChanged {
            viewDataBinding.errorPrompt.visibility = View.GONE
        }
        viewDataBinding.loginPassword.onTextChanged {
            viewDataBinding.errorPrompt.visibility = View.GONE
        }
    }

    private fun hideUIAndShowProgressBar() {
        viewDataBinding.apply {
            loginResponseProgressBar.visibility = View.VISIBLE
            errorPrompt.visibility = View.GONE
            loginEmail.visibility = View.GONE
            loginPassword.visibility = View.GONE
            loginRememberMe.visibility = View.GONE
            loginStayLoggedIn.visibility = View.GONE
            loginButton.visibility = View.GONE
            hideKeyboard(context!!)
        }
    }

    private fun hideProgressBar() {
        viewDataBinding.apply {
            errorPrompt.visibility = View.VISIBLE
            loginEmail.visibility = View.VISIBLE
            loginPassword.visibility = View.VISIBLE
            loginRememberMe.visibility = View.VISIBLE
            loginStayLoggedIn.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            loginResponseProgressBar.visibility = View.GONE
        }
    }

    private fun hideKeyboard(context: Context) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    private fun getUserId(): String {
        return arguments?.let {
            LoginFragmentArgs.fromBundle(it).USERID
        }!!
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorSnackbar)) {
        viewDataBinding.viewmodel?.let {

            view?.setupSnackbar(this, it.snackbarText, length, bgColor)
        }
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openRegisterEvent?.observe(this, EventObserver {
            navigateToRegisterFrag()
        })
        viewDataBinding.viewmodel?.openTaskListEvent?.observe(this, EventObserver {
            navigateToTaskActivity(it)
        })
    }

    private fun navigateToTaskActivity(userUid: String) {
        val action = LoginFragmentDirections.actionLoginFragmentToTasksFragment()
        val intent = Intent(context, TasksActivity::class.java)
        intent.putExtra("USER_ID", userUid)
        startActivity(intent)
        findNavController().navigate(action)
    }

    private fun navigateToRegisterFrag() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(null.toString())
        findNavController().navigate(action)
    }

    private fun checkIfInternetAvailable() {
        if (!viewDataBinding.viewmodel!!.checkNetworkConnection(activity as AppCompatActivity))
            navigateToTaskActivity()
    }

    private fun checkIfUserIsAlreadyLoggedIn() {
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            navigateToTaskActivity()
        } else {
            return
        }
    }

    private fun navigateToTaskActivity() {
        val action = LoginFragmentDirections.actionLoginFragmentToTasksFragment()
        val intent = Intent(context, TasksActivity::class.java)
        startActivity(intent)
        findNavController().navigate(action)
    }
}