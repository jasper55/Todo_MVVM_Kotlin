package com.example.android.architecture.blueprints.todoapp.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.LoginFragmentBinding
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.setupDismissableSnackbar
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbarMessage
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private lateinit var viewDataBinding: LoginFragmentBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        viewModel = obtainViewModel(LoginViewModel::class.java)
        viewDataBinding = LoginFragmentBinding.bind(root).apply {
            viewmodel = viewModel
            listener = object : UserActionsNavigationListener{

                override fun onLoginClicked() {
                    viewDataBinding.viewmodel?.loginUser()
                    //user.uid?.let { viewDataBinding.viewmodel?.loginUser(it) }

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
        //setupSnackbarMessage()
        setupDismissableSnackbar()
    }

    private fun getUserId(): String {
        return arguments?.let {
            LoginFragmentArgs.fromBundle(it).USERID
        }!!
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorTextPrimary)) {
        viewDataBinding.viewmodel?.let {

            view?.setupSnackbar(this, it.snackbarText, length, bgColor)
        }
    }

    private fun setupSnackbarMessage(length: Int = Snackbar.LENGTH_LONG, bgColor: Int = context!!.getColor(R.color.colorRed)) {
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbarMessage(this, it.snackbarMessage, length, bgColor)
        }
    }

    private fun setupDismissableSnackbar(length: Int = Snackbar.LENGTH_LONG) {
        viewDataBinding.viewmodel?.let {
            view?.setupDismissableSnackbar(this,it.errorMessageEvent, length)
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
        val intent = Intent(context,TasksActivity::class.java)
        intent.putExtra("USER_ID",userUid)
        startActivity(intent)
        findNavController().navigate(action)
    }

    private fun navigateToRegisterFrag() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(null.toString())
        findNavController().navigate(action)
    }
}